package com.bsi.dms.prompt;

import com.bsi.dms.R;
import com.bsi.dms.config.PlayerApplication;

import android.os.Handler;
import android.os.HandlerThread;

public class NetworkStatusWatcher {
	private static final int CHECK_STATUS_INTERNAL = 15 * 1000;
	private static NetworkStatusWatcher instance;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private Runnable mCheckStatusRunnable;
	private boolean firstRun = true;
	private boolean previousOnline = false;
	private boolean currentOnline = false;

	public synchronized static NetworkStatusWatcher getInstance() {
		if (instance == null) {
			instance = new NetworkStatusWatcher();
		}
		return instance;
	}

	private NetworkStatusWatcher() {
		mHandlerThread = new HandlerThread("NetworkStatusWatcher");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mCheckStatusRunnable = new CheckStatusRunnable();
	}

	public void start() {
		mHandler.post(mCheckStatusRunnable);
	}

	public void stop() {
		mHandler.removeCallbacks(mCheckStatusRunnable);
	}

	class CheckStatusRunnable implements Runnable {
		@Override
		public void run() {
			currentOnline = PlayerApplication.isNetworkAvailable();
			if (firstRun && !currentOnline) {
				firstRun = false;
				PromptManager.getInstance().toast(R.string.networkOffline,
						PromptManager.ID_NETWORK);
			} else if (!previousOnline && currentOnline) {
				PromptManager.getInstance().toast(R.string.networkOnline,
						PromptManager.ID_NETWORK);
			} else if (previousOnline && !currentOnline) {
				PromptManager.getInstance().toast(R.string.networkOffline,
						PromptManager.ID_NETWORK);
			}
			previousOnline = currentOnline;
			mHandler.postDelayed(this, CHECK_STATUS_INTERNAL);
		}

	}
	
	public boolean isOnline(){
		return currentOnline;
	}

}
