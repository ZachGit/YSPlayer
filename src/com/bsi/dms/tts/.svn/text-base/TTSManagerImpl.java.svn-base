package com.bsi.dms.tts;

import android.content.Context;

public class TTSManagerImpl implements TTSManager {
	private static final String TAG = "TTSManagerImpl";
	/**
	 * single instance
	 */
	private static TTSManagerImpl instance;
	private Context mContext;
	private int mEngineType;
	private TTSEngine mEngine;
	private TTSEngine mLocalEngine;
	private TTSEngine mRemoteEngine;

	public synchronized static TTSManagerImpl getInstance(Context c) {
		if (instance == null) {
			instance = new TTSManagerImpl(c);
		}
		return instance;
	}

	private TTSManagerImpl(Context c) {
		mContext = c;
		mEngineType = ENGINE_REMOTE;
		mLocalEngine = XunfeiTTSEngine.getInstance(c);
		mRemoteEngine = WebserviceTTSEngine.getInstance(c);
		validateEngine();
	}

	@Override
	public void setEngine(int engine) {
		mEngineType = engine;
		validateEngine();
	}

	private void validateEngine() {
		if (mEngineType == ENGINE_REMOTE) {
			mEngine = mRemoteEngine;
		} else {
			// default use local engine to reduce server process
			mEngine = mLocalEngine;
		}
	}

	@Override
	public void speak(String text) {
		mEngine.speak(text);
	}

	@Override
	public void stop() {
		mEngine.stop();
	}

	@Override
	public void onDestroy() {
		mLocalEngine.onDestroy();
		mRemoteEngine.onDestroy();
	}

}
