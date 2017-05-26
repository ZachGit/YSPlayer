package com.bsi.dms.tts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.bsi.dms.tts.TTSData.TTSDataState;

public class SoundManagerImpl implements SoundManager {
	private static final String TAG = "SoundManagerImpl";
	private static final String TTS_TEMP_FILE = "tts_temp";
	private static SoundManagerImpl instance;
	private Context mContext;
	private MediaPlayer mediaPlayer;
	private Queue<TTSData> mTTSDataQueue;

	private SoundManagerImpl(Context c) {
		mContext = c;
		mTTSDataQueue = new ArrayBlockingQueue<TTSData>(10);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaPlayer.stop();
				mTTSDataQueue.poll();
				playNextInQueue();
			}
		});
	}

	protected void playNextInQueue() {
		TTSData ttsData = mTTSDataQueue.peek();
		if (ttsData != null) {
			ttsData.state = TTSDataState.PLAYING;
			playSound(ttsData);
		}
	}

	public synchronized static SoundManagerImpl getInstance(Context c) {
		if (instance == null) {
			instance = new SoundManagerImpl(c);
		}
		return instance;
	}

	@Override
	public void play(TTSData ttsData) {
		if (ttsData == null) {
			return;
		}
		if (mTTSDataQueue.isEmpty()) {
			ttsData.state.equals(TTSData.TTSDataState.PLAYING);
			mTTSDataQueue.add(ttsData);
			playSound(ttsData);
		} else {
			ttsData.state = TTSDataState.WAIT;
			mTTSDataQueue.add(ttsData);
		}
	}

	private synchronized void playSound(TTSData ttsData) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File tempAudioFile = null;
		try {
			tempAudioFile = File.createTempFile(TTS_TEMP_FILE, ".wav",
					mContext.getCacheDir());
			tempAudioFile.deleteOnExit();
			fos = new FileOutputStream(tempAudioFile);
			fos.write(ttsData.data);
			fos.close();
			fis = new FileInputStream(tempAudioFile);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (tempAudioFile != null) {
				tempAudioFile.delete();
			}
		}
	}

	@Override
	public void stop() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mTTSDataQueue.clear();
	}

}
