package com.bsi.dms.tts;

public interface TTSEngine {
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
	 * Called when context destroyed
	 */
	public void onDestroy();
}
