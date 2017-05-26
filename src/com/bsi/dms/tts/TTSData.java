package com.bsi.dms.tts;

/**
 * Wrap tts sound data
 * @author Zuoshu
 *
 */
public class TTSData {
	/**
	 * 
	 * @author Zuoshu
	 * WAIT->PLAYING->DONE
	 *
	 */
	public static enum TTSDataState {
		WAIT, PLAYING, DONE
	}

	// tts string
	public String text;
	// tts state
	public TTSDataState state;
	// sound data
	public byte[] data;

	public TTSData(String text, TTSDataState state, byte[] data) {
		this.text = text;
		this.state = state;
		this.data = data;
	}
}
