package com.bsi.dms.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ActivityUtil {
	
	public static final int statusExit = 0;
	public static final int statusRefresh = 1; 

	public static boolean isBackground(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

		if (appProcesses.size() == 0)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("鍚庡彴", appProcess.processName);
					return true;
				} else {
					Log.i("鍓嶅彴", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	
	 /**
		 * 鍒ゆ柇鏌愪釜鏈嶅姟鏄惁姝ｅ湪杩愯鐨勬柟娉�
		 * 
		 * @param mContext
		 * @param serviceName
		 *            鏄寘鍚�+鏈嶅姟鐨勭被鍚嶏紙渚嬪锛歯et.loonggg.testbackstage.TestService锛�
		 * @return true浠ｈ〃姝ｅ湪杩愯锛宖alse浠ｈ〃鏈嶅姟娌℃湁姝ｅ湪杩愯
		 */
		public static boolean isServiceWork(Context mContext, String serviceName) {
			
			ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

			if (appProcesses.size() == 0)
				return false;

			for (RunningAppProcessInfo appProcess : appProcesses) {

				if (appProcess.processName.equals(serviceName)) {
					
						return true;						
				}
			}
			return false;
		}

	/**
	 * * 杩斿洖褰撳墠鐨勫簲鐢ㄦ槸鍚﹀浜庡墠鍙版樉绀虹姸鎬� * @param $packageName * @return
	 */
	public static  boolean isForeground(Context context, String packageName) {
		// context鏄竴涓繚瀛樼殑涓婁笅鏂�
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

		if (appProcesses.size() == 0)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i("鍓嶅彴", appProcess.processName);
					return true;
				} else {
					Log.i("鍚庡彴", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	
	/*public static boolean moveTaskToFront(Context context)
	{
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(
                context, Class
                        .forName(className)));//
        // intent.setClass(context,
        // Class.forName(className));
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
	}
*/
	
	public static void startApp(Context context,String packageName)
	{
		PackageManager packageManager = context.getPackageManager();
		 
        // 鑾峰彇鎵嬫満閲岀殑搴旂敤鍒楄〃

        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);

        for (int i = 0; i < pInfo.size(); i++)

        {

            PackageInfo p = pInfo.get(i);

            // 鑾峰彇鐩稿叧鍖呯殑<application>涓殑label淇℃伅锛屼篃灏辨槸-->搴旂敤绋嬪簭鐨勫悕瀛�

            String label = packageManager.getApplicationLabel(p.applicationInfo).toString();

            System.out.println(label);

            if (label.equals(packageName)){ //姣旇緝label  

                String pName = p.packageName; //鑾峰彇鍖呭悕  

                Intent intent = new Intent();  

               //鑾峰彇intent  

                intent =packageManager.getLaunchIntentForPackage(pName);  
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

                context.startActivity(intent);  

            }

        }
	}
}
