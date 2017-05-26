package com.bsi.dms.webservice;

public interface UpdateWebService {
	
	public static final String UPDATE_SERVICE = "/Services/WebService.asmx";

	/**
	 * 
	 * @param versionNo
	 *            客户端版本号
	 * @param equipmentID
	 *            客户端设备号
	 * @param clientType
	 *            android =2 x86=1
	 * @return string[0] 更新包下载地址 无更新则返回"",string[1]
	 *         更新模式(1=立即更新，2=定时更新，3=提示更新),string[2] 更新时间
	 */
	public String[] getLatestUpdateInfo(String versionNo, String equipmentID,
			int clientType);

	/**
	 * Get ftp info
	 * @return string[0] url, string[1] user,string[2] password
	 */
	public String[] getWebInfo();
	
	/**
	 * 
	 * @param equipmentID Equipment Id, get when app register
	 * @param versionNo Current version
	 * @param isPublish 0=success,1=installing,2=fail
	 */
	public void setEquipmentVersion(String equipmentID, String versionNo, int isPublish);
}
