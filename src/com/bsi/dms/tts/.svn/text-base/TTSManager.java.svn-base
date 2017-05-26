package com.bsi.dms.tts;

public interface TTSManager {
	public static final int ENGINE_LOCAL = 1;
	public static final int ENGINE_REMOTE = 2;

	public void setEngine(int engine);

	/**
	 * 
	 * @param text
	 *            Text to speak
	 */
	public void speak(String text);

	/**
	 * Stop convert and speak all pending strings
	 */
	public void stop();
	
	/**
	 * Called when destroyed
	 */
	public void onDestroy();

}
