package com.bsi.dms.update;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bsi.dms.bean.Syscfg;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.deamon.DeamonConst;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.utils.CommonUtil;
import com.bsi.dms.webservice.UpdateWebService;
import com.bsi.dms.webservice.UpdateWebServiceImpl;
import com.bsi.dms.webservice.WebserviceUtil;

public class UpdateManagerImpl implements UpdateManager {
	public static final String INSTALL_IMMEDIATELY_STRING = "0";
	private static final int MIN_UPDATE_WEB_SERVICE_RESPONSE_ARRAY_COUNT = 3;
	private static final int MIN_WEB_INFO_WEB_SERVICE_RESPONSE_ARRAY_COUNT = 3;
	private static final String TAG = "UPDATE";
	private static UpdateManagerImpl instance;

	public synchronized static UpdateManagerImpl getInstance() {
		if (instance == null) {
			instance = new UpdateManagerImpl();
		}
		return instance;
	}

	private UpdateWebService mUpdateWebService;

	private Syscfg mSyscfg;

	private UpdateManagerImpl() {
		if (!isDeamonServiceRunning()) {
			setStatus(Status.IDLE);
			setInstallType("");
			setInstallTime("");
		}
		mSyscfg = PlayerApplication.getInstance().sysconfig;
		String serviceUrl = WebserviceUtil.getUpdateServiceFullUrl();
		mUpdateWebService = new UpdateWebServiceImpl(serviceUrl);
	}

	private String buildTempFilePath(UpdateDetail detail) {
		return CommonUtil.getTempFilePath();
	}

	/**
	 * TODO Add resource files update
	 */
	@Override
	public synchronized void fetchAndUpdate() {
		Log.d(TAG, "====================UPDATE START====================");
		UpdateDetail detail = getLatestUpdate();
		WebInfo webinfo = getWebInfo();
		update(detail, webinfo);
	}

	private UpdateDetail getLatestUpdate() {
		String versionNo = PlayerApplication.getVersion();
		String equipmentId = mSyscfg.getEquipmentId();
		int clientType = PlayerConst.CLIENT_TYPE_ANDROID;
		String[] webserviceResponse = mUpdateWebService.getLatestUpdateInfo(
				versionNo, equipmentId, clientType);
		if (webserviceResponse == null
				|| webserviceResponse.length < MIN_UPDATE_WEB_SERVICE_RESPONSE_ARRAY_COUNT
				|| TextUtils.isEmpty(webserviceResponse[0])) {
			return null;
		}
		UpdateDetail response = new UpdateDetail();
		response.setFilename(webserviceResponse[0]);
		response.setInstallType(webserviceResponse[1]);
		response.setInstallTime(webserviceResponse[2]);
		response.setCurrentVersion(versionNo);
		response.setEquipmentId(equipmentId);
		return response;
	}

	@Override
	public Status getStatus() {
		return Status.valueOf(UpdatePrefUtil.readSavedStatus());
	}

	private WebInfo getWebInfo() {
		String webserviceResponse[] = mUpdateWebService.getWebInfo();
		if (webserviceResponse == null
				|| webserviceResponse.length < MIN_WEB_INFO_WEB_SERVICE_RESPONSE_ARRAY_COUNT) {
			return null;
		}
		WebInfo webinfo = new WebInfo();
		webinfo.setFtpServer(webserviceResponse[0]);
		webinfo.setUsername(webserviceResponse[1]);
		webinfo.setPassword(webserviceResponse[2]);
		return webinfo;
	}

	private boolean isDeamonServiceRunning() {
		ActivityManager manager = (ActivityManager) PlayerApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningTaskInfos = manager
				.getRunningServices(Integer.MAX_VALUE);
		if (runningTaskInfos == null || runningTaskInfos.size() == 0) {
			return false;
		}
		for (RunningServiceInfo info : runningTaskInfos) {
			if (info.service.getPackageName().equals("com.bsi.dms.deamon")
					&& info.started) {
				return true;
			}
		}
		return false;
	}

	private void setInstallTime(String time) {
		UpdatePrefUtil.saveInstallTime(time);
	}

	private void setInstallType(String type) {
		UpdatePrefUtil.saveInstallType(type);
	}

	protected void setStatus(Status status) {
		Log.d(TAG, "status->" + status.toString());
		UpdatePrefUtil.saveStatus(status);
	}

	@Override
	public void update(String installType, String installTime) {
		UpdateDetail detail = getLatestUpdate();
		if (detail != null) {
			detail.setInstallType(installType);
			detail.setInstallTime(installTime);
		}
		WebInfo webinfo = getWebInfo();
		update(detail, webinfo);
	}

	/**
	 * Download apk, and invoke DeamonService to install
	 * 
	 * @param detail
	 *            update detail info
	 * @param webinfo
	 */
	private void update(UpdateDetail detail, WebInfo webinfo) {
		Status currentStatus = getStatus();
		String currentInstallType = UpdatePrefUtil.readInstallType();
		// Skip when there is an immediately installing being processed
		if (currentStatus.equals(Status.INSTALLING)
				&& currentInstallType != null
				&& currentInstallType
						.equals(UpdateDetail.UPDATE_TYPE_IMMEDIATELY)) {
			Log.d(TAG,
					"There is already a update being processed, skip current.status:"
							+ getStatus() + " type:"
							+ UpdatePrefUtil.readInstallType() + " time:"
							+ UpdatePrefUtil.readInstallTime());
			return;
		}
		if (detail == null) {
			Log.d(TAG,
					"No update available!versionNo:"
							+ PlayerApplication.getVersion() + " equipmentId:"
							+ mSyscfg.getEquipmentId());
			return;
		}
		if (webinfo == null) {
			Log.d(TAG,
					"Can not get webinfo!versionNo:"
							+ PlayerApplication.getVersion() + " equipmentId:"
							+ mSyscfg.getEquipmentId());
			return;
		}
		// if install time is "0", means install immediately."01:00" means install at 01:00
		if(detail.getInstallTime().equals(INSTALL_IMMEDIATELY_STRING)){
			detail.setInstallType(UpdateDetail.UPDATE_TYPE_IMMEDIATELY);
		}else{
			detail.setInstallType(UpdateDetail.UPDATE_TYPE_TIMED);
		}
		setStatus(Status.DOWNLOAING);
		String tempDir = buildTempFilePath(detail);
		File tempFileDir = new File(tempDir);
		String filePath = detail.getFilename();
		if (TextUtils.isEmpty(filePath)) {
			Log.d(TAG, "filepath empty in UpdateDetail, skip update:"
					+ PlayerApplication.getVersion() + " equipmentId:"
					+ mSyscfg.getEquipmentId());
			return;
		}
		String fileSavePath = tempFileDir + File.separator
				+ filePath.substring(filePath.lastIndexOf('/') + 1);
		Log.d(TAG,
				"download ftp server:" + webinfo.getFtpServer() + " filepath:"
						+ detail.getFilename() + " username:"
						+ CommonUtil.genMaskString(webinfo.getUsername(), '*')
						+ " password:"
						+ CommonUtil.genMaskString(webinfo.getPassword(), '*')
						+ " filesavepath:" + fileSavePath);
		boolean success = DownloadEngine.getInstance().downloadFtp(
				webinfo.getFtpServer(), detail.getFilename(),
				webinfo.getUsername(), webinfo.getPassword(), fileSavePath);
		if (!success) {
			Log.e(TAG, "download update error!");
			setStatus(Status.IDLE);
			return;
		}
		

		CommonUtil.chmodFileAndDir(new File(fileSavePath), "777");
		setStatus(Status.INSTALLING);
		setInstallType(detail.getInstallType());
		setInstallTime(detail.getInstallTime());
		
//		Intent i = new Intent(Intent.ACTION_VIEW);
//		i.setDataAndType(Uri.parse("file://" + fileSavePath), "applicationnd.android.package-archive");
//		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		PlayerApplication.getInstance().startActivity(i);
		// Install downloaded apk in deamon app to start after install
//		CommonUtil.installApk(fileSavePath);
		Intent deamonIntent = new Intent();
		deamonIntent.setAction(DeamonConst.SERVICE_ACTION);
		deamonIntent.putExtra(DeamonConst.INTENT, DeamonConst.INSTALL);
		deamonIntent.putExtra(DeamonConst.INSTALL_FILE, fileSavePath);
		deamonIntent
				.putExtra(DeamonConst.INSTALL_TYPE, detail.getInstallType());
		deamonIntent
				.putExtra(DeamonConst.INSTALL_TIME, detail.getInstallTime());
		deamonIntent.putExtra(DeamonConst.INSTALL_CURRENT_VERSION,
				detail.getCurrentVersion());
		PlayerApplication.getInstance().startService(deamonIntent);
	}

	@Override
	public void reportCurrentVersion() {
		WebserviceUtil.reportCurrentVersion();
	}

	@Override
	public boolean updateDeamon() {
		int requriedDeamonVersion = PlayerApplication
				.getRequiredDeamonVersion();
		int installedDeamonVersion = CommonUtil.getInstalledDeamonVersion();
		Log.d(TAG, "Updating deamon, requriedDeamonVersion="
				+ requriedDeamonVersion + " installedDeamonVersion="
				+ installedDeamonVersion);
		if (installedDeamonVersion < requriedDeamonVersion) {
			if (!installDeamonFromAsserts()) {
				Log.d(TAG, "install deamon fail!");
				return false;
			} else {
				int newInstalledDeamonVersion = CommonUtil
						.getInstalledDeamonVersion();
				Log.d(TAG, "install deamon success!requriedDeamonVersion="
						+ requriedDeamonVersion + " installedDeamonVersion="
						+ newInstalledDeamonVersion);
			}
		}
		return true;
	}

	private boolean installDeamonFromAsserts() {
		String srcFile = "Deamon.apk";
		String destFile = CommonUtil.getTempFilePath() + File.separator
				+ "Deamon.apk";
		CommonUtil.ensureFileExist(destFile);
		if (!CommonUtil.copyAssertsToSdcard(srcFile, destFile)) {
			return false;
		}
		CommonUtil.installApk(destFile);
		return true;
	}

}
