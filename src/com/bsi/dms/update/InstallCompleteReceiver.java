package com.bsi.dms.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.update.UpdateManager.Status;
import com.bsi.dms.webservice.WebserviceUtil;

public class InstallCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "UPDATE";

	private void changeInstallStatus(Context c) {
		Log.d(TAG, "receive install complete,set status IDLE");
		UpdatePrefUtil.saveStatus(Status.IDLE);
		UpdatePrefUtil.saveInstallTime("");
		UpdatePrefUtil.saveInstallType("");
	}

	@Override
	public void onReceive(Context c, Intent i) {
		Log.d(TAG, "InstallCompleteReceiver receive broadcast,current version:"+PlayerApplication.getVersion());
		changeInstallStatus(c);
		new Thread(new Runnable() {
			@Override
			public void run() {
				WebserviceUtil.reportInstallSuccess();
				Log.d(TAG, "=====UPDATE FINISH=====");
			}
		}).start();
	}

}
