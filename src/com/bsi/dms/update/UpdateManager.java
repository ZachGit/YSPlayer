package com.bsi.dms.update;

public interface UpdateManager {
	public static enum Status {
		IDLE, DOWNLOAING, INSTALLING
	};

	/**
	 * Download and update if update is available, run in the calling thread
	 */
	public void fetchAndUpdate();

	public void update(String installType,String installTime);

	/**
	 * 
	 * @return update status
	 */
	public Status getStatus();
	
	public void reportCurrentVersion();
	
	public boolean updateDeamon();
}
