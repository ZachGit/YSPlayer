package com.bsi.dms.tts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.widget.Toast;

import com.bsi.dms.utils.CommonUtil;


/**

 * 
 */
public class ApkInstaller {
	/**
	 * @param context
	 * @param assetsApk
	 */
	public static boolean installFromAssets(Context context, String assetsApk) {
		try {
			AssetManager assets = context.getAssets();
			// 
			InputStream stream;
			stream = assets.open(assetsApk);
			if (stream == null) {
				Toast.makeText(context, "assets no apk", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			String folder = CommonUtil.getTTSApkPath() ;
			File f = new File(folder);
			if (!f.exists()) {
				f.mkdir();
			}
			
			String apkPath = CommonUtil.getTTSApkPath() + File.separator +"SpeechService.apk";
			File file = new File(apkPath);
			
			if (!writeStreamToFile(stream, file)) {
				return false;
			}
			if( CommonUtil.isKaiboer() ){
				CommonUtil.chmodFileAndDir(file, "777");
			}
			installApk(context, apkPath);
		} catch (IOException e) {
			e.printStackTrace();			
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param context
	 * @param url
	 */
	public static void openDownloadWeb(Context context, String url) {
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(it);
	}

	public static boolean writeStreamToFile(InputStream stream, File file) {
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			final byte[] buffer = new byte[1024];
			int read;
			while ((read = stream.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			output.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		} finally {
			try {
				output.close();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	private static void installApk(Context context, String apkPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(apkPath)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
