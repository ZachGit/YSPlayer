package com.bsi.dms.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.update.UpdateManager.Status;

public class UpdatePrefUtil {
	private static final String TAG = "UpdatePrefUtil";
	public static final String UPDATE_PREFS_NAME = "update";
	public static final String UPDATE_PREFS_KEY_STATUS = "status";
	public static final String UPDATE_PREFS_KEY_INSTALL_TYPE = "type";
	public static final String UPDATE_PREFS_KEY_TIME = "time";

	public static String readInstallTime() {
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		return updateInfo.getString(UPDATE_PREFS_KEY_TIME, "");
	}

	public static String readInstallType() {
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		return updateInfo.getString(UPDATE_PREFS_KEY_INSTALL_TYPE, "");
	}

	public static String readSavedStatus() {
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		return updateInfo.getString(UPDATE_PREFS_KEY_STATUS,
				Status.IDLE.toString());
	}

	public static void saveInstallTime(String time) {
		if (time == null) {
			time = "";
		}
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		updateInfo.edit().putString(UPDATE_PREFS_KEY_TIME, time).commit();
	}

	public static void saveInstallType(String type) {
		if (type == null) {
			type = "";
		}
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		updateInfo.edit().putString(UPDATE_PREFS_KEY_INSTALL_TYPE, type)
				.commit();
	}

	public static void saveStatus(Status status) {
		if (status == null) {
			status = Status.IDLE;
			Log.d(TAG, "status null, save as IDLE!");
		}
		SharedPreferences updateInfo = PlayerApplication.getInstance()
				.getSharedPreferences(UPDATE_PREFS_NAME, Context.MODE_PRIVATE);
		updateInfo.edit().putString(UPDATE_PREFS_KEY_STATUS, status.toString())
				.commit();
	}
}
