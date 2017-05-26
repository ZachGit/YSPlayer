package com.bsi.dms;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.bsi.dms.tts.ApkInstaller;
import com.bsi.dms.utils.CommonUtil;
import com.iflytek.speech.SpeechUtility;

public class CheckServiceInstallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		if (SpeechUtility.getUtility(this).queryAvailableEngines() == null
				|| SpeechUtility.getUtility(this).queryAvailableEngines().length <= 0) {
			String assetsApk = "SpeechService.apk";
			processInstall(CheckServiceInstallActivity.this, assetsApk);
		}
		*/
		if(!CommonUtil.isDeamonInstalled()){
			processInstall(this, "Deamon.apk");
		}
		finish();
	}

	private void processInstall(Context context, String assetsApk) {
		if (!ApkInstaller.installFromAssets(context, assetsApk)) {
			Toast.makeText(CheckServiceInstallActivity.this, "安装失败！",
					Toast.LENGTH_SHORT).show();
		}
	}
	
}
