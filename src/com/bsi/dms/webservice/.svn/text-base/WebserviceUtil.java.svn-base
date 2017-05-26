package com.bsi.dms.webservice;

import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConst;

public class WebserviceUtil {
	public static final int SUCCESS = 0;
	public static final int INSTALLING = 1;
	public static final int FAIL = 2;

	public static void reportInstallSuccess() {
		reportInstallStatus(SUCCESS);
	}

	public static void reportInstallFail() {
		reportInstallStatus(FAIL);
	}
	
	public static void reportCurrentVersion(){
		reportInstallStatus(SUCCESS);
	}

	public static void reportInstallStatus(int isPublish) {
		String equipmentID = PlayerApplication.sysconfig.getEquipmentId();
		String versionNo = PlayerApplication.getVersion();
		UpdateWebService service = new UpdateWebServiceImpl(
				getUpdateServiceFullUrl());
		service.setEquipmentVersion(equipmentID, versionNo, isPublish);
	}

	public static String getUpdateServiceFullUrl() {
		return PlayerApplication.getInstance().sysconfig.getServerVirtualDir()
				+ UpdateWebService.UPDATE_SERVICE;
	}

}
