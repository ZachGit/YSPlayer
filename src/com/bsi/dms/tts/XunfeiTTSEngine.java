package com.bsi.dms.tts;

import android.content.Context;
import android.os.RemoteException;

import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

public class XunfeiTTSEngine implements TTSEngine, InitListener {
	private static final String TAG = "XunfeiTTSEngine";
	private static XunfeiTTSEngine instance;
	private Context mContext;
	private SpeechSynthesizer mTts;

	public static synchronized XunfeiTTSEngine getInstance(Context c) {
		if (instance == null) {
			instance = new XunfeiTTSEngine(c);
		}
		return instance;
	}

	private XunfeiTTSEngine(Context c) {
		mContext = c;
		// SpeechUtility.getUtility(mContext).setAppid("51b152f7");
		mTts = new SpeechSynthesizer(mContext, this);
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "vixx");
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");
		mTts.setParameter(SpeechSynthesizer.PITCH, "50");
	}

	@Override
	public void speak(String text) {
		mTts.startSpeaking(text, mTtsListener);
	}

	@Override
	public void stop() {
		mTts.stopSpeaking(mTtsListener);
	}

	@Override
	public void onDestroy() {
		mTts.destory();
	}

	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			// Log.d(TAG, "onBufferProgress:" + progress);
		}

		@Override
		public void onCompleted(int code) throws RemoteException {
			// Log.d(TAG, "onCompleted:" + code);
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			// Log.d(TAG, "onSpeakBegin");
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			// Log.d(TAG, "onSpeakPaused");
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			// Log.d(TAG, "onSpeakProgress:" + progress);
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			// Log.d(TAG, "onSpeakResumed");
		}
	};

	@Override
	public void onInit(ISpeechModule module, int code) {
		// Log.d(TAG, "onInit:" + code);
	}

}
