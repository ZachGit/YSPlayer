package com.bsi.dms.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.onaips.vnc.ResLoader;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bsi.dms.R;
import com.bsi.dms.bean.Syscfg;
import com.bsi.dms.parse.SyscfgHandler;
import com.bsi.dms.prompt.PromptManager;
import com.bsi.dms.utils.CommonUtil;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class PlayerApplication extends Application {
	private static final String TAG = "PlayerApplication";
	private static final String JS_TAG = "JSCSS";
	private static PlayerApplication mPlayerApplication;
	private static Context sContext;
	public static Syscfg sysconfig;
	public final boolean showStatusBar = false;
	private static final int BUFFER_SIZE = 1024 * 5;
	private static final String JS_CSS_ZIP_FILE_NAME = "jsCss.zip";

	public synchronized static PlayerApplication getInstance() {
		return mPlayerApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayerApplication = this;
		sContext = getApplicationContext();
		try {
			getSyscfg();
		} catch (Throwable e) {
			PromptManager.getInstance().toast(R.string.readConfigError,
					PromptManager.ID_SETTING);
			e.printStackTrace();
		}

		// vnc
		// if (firstRun()){
		// }
		createBinaries();
	}

	public boolean firstRun() {
		int versionCode = 0;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_META_DATA).versionCode;
		} catch (NameNotFoundException e) {
			log("Package not found... Odd, since we're in that package..."
					+ e.getMessage());
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int lastFirstRun = prefs.getInt("last_run", 0);

		if (lastFirstRun >= versionCode) {
			log("Not first run");
			return false;
		}
		log("First run for version " + versionCode);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("last_run", versionCode);
		editor.commit();
		return true;
	}

	public void log(String s) {
	}

	public void createBinaries() {
		String filesdir = getFilesDir().getAbsolutePath() + "/";

		// copy html related stuff
		copyBinary(R.raw.webclients, filesdir + "/webclients.zip");

		try {
			ResLoader.unpackResources(R.raw.webclients,
					getApplicationContext(), filesdir);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void copyBinary(int id, String path) {
		log("copy -> " + path);
		try {
			InputStream ins = getResources().openRawResource(id);
			int size = ins.available();

			// Read the entire resource into a local byte buffer.
			byte[] buffer = new byte[size];
			ins.read(buffer);
			ins.close();

			FileOutputStream fos = new FileOutputStream(path);
			fos.write(buffer);
			fos.close();
		} catch (Exception e) {
			log("public void createBinary() error! : " + e.getMessage());
		}
	}

	public static Context getAppContext() {
		if (sContext == null)
			Log.e(TAG, "Global context not set");
		return sContext;
	}

	// see AndroidManifest.xml
	public static String getVersion() {
		String versionName = "";
	    try {  
	        PackageManager pm = getInstance().getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(getInstance().getPackageName(), 0);  
	        versionName = pi.versionName;
	        if(versionName==null){
	        	versionName = "";
	        }
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);
	    }
	    return versionName;
	}

	public static int getRequiredDeamonVersion() {
		return 2;
	}

	public void getSyscfg() throws Throwable {
		InputStream inputStream = null;
		String cfgPath = CommonUtil.getSyscfgPath() + File.separator
				+ "syscfg.xml";
		File cfgFile = new File(cfgPath);
		if (cfgFile.exists()) {
			Log.i(TAG, "get local config info");
			if (cfgFile.length() == 0) {
				File bakFile = new File(CommonUtil.getSyscfgPath()
						+ File.separator + "syscfg.xml.bak");
				if (bakFile.exists() && bakFile.length() != 0) {
					inputStream = new FileInputStream(bakFile);
				} else {
					inputStream = this.getClass().getClassLoader()
							.getResourceAsStream("syscfg.xml");
				}

			} else {
				inputStream = new FileInputStream(cfgFile);
			}
		} else {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream("syscfg.xml");
		}
		SyscfgHandler pHandler = new SyscfgHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		saxParser.parse(inputStream, pHandler);
		List<Syscfg> cfgs = pHandler.getSyscfgs();
		sysconfig = cfgs.get(0);
		inputStream.close();
	}

	public boolean copyJsFile() {
		Log.w(TAG, "come into copyJsFile");
		String jsPath = CommonUtil.getMediaBasePath() + File.separator
				+ "PlayList";
		String outputJsFileName = jsPath + File.separator
				+ JS_CSS_ZIP_FILE_NAME;
		String destJsCssDir = jsPath;
		if (!copyJsZip(outputJsFileName)) {
			return false;
		}
		File outputJsZip = new File(outputJsFileName);
		Log.w(JS_TAG, "copied jsCss.zip length:" + outputJsZip.length());
		if (!unzipFile(outputJsFileName, destJsCssDir)) {
			return false;
		}
		CommonUtil.setJSExist();
		return true;
	}

	private boolean copyJsZip(String outFileName) {
		CommonUtil.ensureFileExist(outFileName);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = getAssets().open(JS_CSS_ZIP_FILE_NAME);
			int jsCssAssertLength = inputStream.available();
			Log.w(JS_TAG, "jsCss.zip length:" + jsCssAssertLength);
			outputStream = new FileOutputStream(outFileName);
			byte[] buffer = new byte[BUFFER_SIZE];
			int readLength = 0;
			int totalLength = 0;
			while ((readLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readLength);
				totalLength += readLength;
			}
			Log.w(JS_TAG, "copy zip length:" + totalLength);
		} catch (IOException e) {
			Log.e(TAG, "copy JS file fail!");
			e.printStackTrace();
			return false;
		} finally {
			CommonUtil.closeStream(outputStream);
			CommonUtil.closeStream(inputStream);
		}
		return true;
	}

	public boolean unzipFile(String srcZipFileName, String destDir) {
		ZipInputStream zipInputStream = null;
		ZipEntry entry = null;
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(
					srcZipFileName));
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String zipPath = entry.getName();
				try {
					if (entry.isDirectory()) {
						File zipFolder = new File(destDir + File.separator
								+ zipPath);
						if (!zipFolder.exists()) {
							zipFolder.mkdirs();
						}
					} else {
						copyZipEntry(zipInputStream, destDir + File.separator
								+ zipPath);
					}
				} catch (Exception e) {
					Log.e(TAG, "unzip file error zippath=" + zipPath);
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "unzip file error");
			e.printStackTrace();
			return false;
		} finally {
			CommonUtil.closeStream(zipInputStream);
		}
		return true;
	}

	private void copyZipEntry(ZipInputStream zipInputStream, String destFileName) {
		FileOutputStream fos = null;
		try {
			if (zipInputStream == null || zipInputStream.available() <= 0) {
				return;
			}
			File file = new File(destFileName);
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int readLength = 0;
			while ((readLength = zipInputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, readLength);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void saveSysconfig() {
		String strIP = sysconfig.getServerip();
		String strPort = sysconfig.getServerport();
		String strPlayid = sysconfig.getPlayid();
		String strHBtime = sysconfig.getHeartbeattime().toString();
		String strBaseurl = sysconfig.getBaseurl();
		String strEquipmentId = sysconfig.getEquipmentId();
		String strServerVirtualDir = sysconfig.getServerVirtualDir();
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 生成xml文件
		String strSyscfg = xmlCreate.createConfigXml(strIP, strPort, strPlayid,
				strHBtime, strBaseurl, strEquipmentId, strServerVirtualDir);

		// 将设置后的数据保存到syscfg.xml中
		try {
			CommonUtil.writeCfgXml(strSyscfg, "syscfg.xml");
		} catch (IOException e) {
			PromptManager.getInstance().toast(R.string.writeConfigError,
					PromptManager.ID_NETWORK);
			e.printStackTrace();
		}

	}

	public synchronized void saveSysconfigbak() {
		if (CommonUtil.isFileExist(CommonUtil.getSyscfgPath() + File.separator
				+ "syscfg.xml.bak")) {
			return;
		}
		String strIP = sysconfig.getServerip();
		String strPort = sysconfig.getServerport();
		String strPlayid = sysconfig.getPlayid();
		String strHBtime = sysconfig.getHeartbeattime().toString();
		String strBaseurl = sysconfig.getBaseurl();
		String strEquipmentId = sysconfig.getEquipmentId();
		String strServerVirtualDir = sysconfig.getServerVirtualDir();
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 生成xml文件
		String strSyscfg = xmlCreate.createConfigXml(strIP, strPort, strPlayid,
				strHBtime, strBaseurl, strEquipmentId, strServerVirtualDir);

		// 将设置后的数据保存到syscfg.xml中
		try {
			CommonUtil.writeCfgXml(strSyscfg, "syscfg.xml.bak");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long getMemoryPercent() {
		long memfact = 0;
		final ActivityManager activityManager = (ActivityManager) sContext
				.getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		// outInfo.totalMem
		if (outInfo != null && outInfo.totalMem != 0) {
			memfact = (outInfo.totalMem - outInfo.availMem) * 100
					/ outInfo.totalMem;
		}
		return memfact;
	}

	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) sContext
				.getSystemService(sContext.CONNECTIVITY_SERVICE);
		if (cm == null) {
			Toast.makeText(sContext, "网络未连接", Toast.LENGTH_LONG).show();
		} else {
			if (cm.getActiveNetworkInfo() != null) {
				return cm.getActiveNetworkInfo().isAvailable();
			}
			/*
			 * NetworkInfo[] info = cm.getAllNetworkInfo(); if (info != null) {
			 * for (int i = 0; i < info.length; i++) { if (info[i].getState() ==
			 * NetworkInfo.State.CONNECTED) { return true; } } }
			 */
		}
		return false;
	}
}
