package com.bsi.dms.update;

public class UpdateDetail {
	/**
	 * install downloaded apk immediately
	 */
	public static final String UPDATE_TYPE_IMMEDIATELY = "1";
	/**
	 * install downloaded apk at some time set by installTime
	 */
	public static final String UPDATE_TYPE_TIMED = "2";

	// download filename
	private String filename;
	// how to install downloaded apk
	private String installType;
	// if intallType == UPDATE_TYPE_TIMED, use installTime to set install time
	private String installTime;
	// version before update
	private String currentVersion;

	private String equipmentId;

	public String getCurrentVersion() {
		return currentVersion;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public String getFilename() {
		return filename;
	}

	public String getInstallTime() {
		return installTime;
	}

	public String getInstallType() {
		return installType;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setInstallTime(String installTime) {
		this.installTime = installTime;
	}

	public void setInstallType(String installType) {
		this.installType = installType;
	}

	public String toString() {
		return "filename=" + filename + " type=" + installType + " time="
				+ installTime + " currentVersion=" + currentVersion
				+ " equipmentId=" + equipmentId;
	}

}
