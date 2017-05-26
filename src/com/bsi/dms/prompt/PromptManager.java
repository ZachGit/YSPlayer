package com.bsi.dms.prompt;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bsi.dms.config.PlayerApplication;

public class PromptManager {
	private class ToastArgs {
		String toastStr;
		String id;
	}

	private static final String TAG = "PromptManager";
	public static final int MAX_TOAST_COUNT = 50;
	private static final int TOAST = 1;
	private static PromptManager instance;
	// make sure to sychronize
	private HashMap<String, Integer> toastCounter;
	public static final String ID_DEFAULT = "DEFAULT";
	public static final String ID_REGISTER = "REGISTER";
	public static final String ID_NETWORK = "NETWORK";
	public static final String ID_COMMAND = "COMMAND";
	public static final String ID_PLAY = "PLAY";
	public static final String ID_SETTING = "SETTING";

	public synchronized static PromptManager getInstance() {
		if (instance == null) {
			instance = new PromptManager();
		}
		return instance;
	}

	private HandlerThread mHandlerThread;

	private Handler mHandler;

	private PromptManager() {
		toastCounter = new HashMap<String, Integer>();
		mHandlerThread = new HandlerThread("PromptManager");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TOAST:
					ToastArgs args = (ToastArgs) msg.obj;
					processToast(args.toastStr, args.id);
					break;
				}
			}

		};
	}

	private void makeToast(String toastStr, String id) {
		Context context = PlayerApplication.getInstance();
		String concatString = id + "\n" + toastStr;
		Toast.makeText(context, concatString, Toast.LENGTH_LONG).show();
	}

	private void processToast(String toastStr, String id) {
		if (TextUtils.isEmpty(toastStr)) {
			Log.e(TAG, "toast string empty,skip toast!");
			return;
		}
		if (TextUtils.isEmpty(id)) {
			id = ID_DEFAULT;
		}
		Integer count = toastCounter.get(id);
		if (count == null) {
			count = Integer.valueOf(0);
		}
		if (count >= MAX_TOAST_COUNT) {
			Log.e(TAG, "toast too many times,skip toast.String:" + toastStr
					+ " id:" + id);
			return;
		}
		count++;
		toastCounter.put(id, count);
		makeToast(toastStr, id);
	}

	private void toast(String toastStr, String id) {
		ToastArgs args = new ToastArgs();
		args.toastStr = toastStr;
		args.id = id;
		Message msg = new Message();
		msg.what = TOAST;
		msg.obj = args;
		mHandler.sendMessage(msg);
	}

	public void toast(int strId, String id) {
		Context context = PlayerApplication.getInstance();
		String str = "";
		try {
			str = context.getResources().getString(strId);
		} catch (Exception e) {
			Log.e(TAG, "String resource not found!Skip toast!");
			return;
		}
		toast(str, id);
	}

	public void toast(int strId) {
		toast(strId, ID_DEFAULT);
	}

}
