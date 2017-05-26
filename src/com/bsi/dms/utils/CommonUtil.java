package com.bsi.dms.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bsi.dms.bean.Programtask;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.tts.ApkInstaller;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class CommonUtil {
	private static final String TAG = "CommonUtil";
	public static final String FLAG_FILE_CONTENT = "1";

	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	public static boolean isFileExist(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void ensureFileExist(String path) {
		File file = new File(path);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static long getFreeSpaceSize() {
		File path = new File(CommonUtil.getSDPath());
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		Date date = new Date(System.currentTimeMillis());
		String systemTime = sDateFormat.format(date);
		return systemTime;
	}

	public static Date stringToDate(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getEndTime(String startTime, String duration) {
		Date date = stringToDate(startTime);
		long times = date.getTime() + Long.parseLong(duration);
		Date endTime = new Date(times);
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		return sDateFormat.format(endTime);
	}

	public static long getTimeDif(String startTime, String endTime) {
		Date begin = CommonUtil.stringToDate(startTime);
		Date end = CommonUtil.stringToDate(endTime);
		long delayMillis = end.getTime() - begin.getTime();
		return delayMillis;
	}

	public static void sleepIgnoreInterupt(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sDate = sdf.format(new Date());
		return sDate;
	}

	// yyyy/MM/dd HH:mm:ss
	public static void setDate(String datetime) {
		SimpleDateFormat sdf = null;
		if (datetime == null) {
			return;
		}
		if (datetime.contains("-")) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		} else {
			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
		}
		Date date = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			Log.e("CommonUtil", "parse datetime error");
			return;
			// e.printStackTrace();
		}
		long millisec = date.getTime();
		if (millisec / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(millisec);
		}
	}

	public static String getSDPath() {
		String sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getPath();
		}

		if (sdDir == null) {
			if (isKaiboer()) {
				sdDir = "/mnt/nand"; // for kaiboer
			} else {
				Log.e("CommUtil", "Enter mygica storage");
				sdDir = "/sdcard"; // for mygica
			}
		}
		Log.e("CommUtil", "player dir------" + sdDir);

		return sdDir;
	}

	public static String getTaskBasePath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir + File.separator + "TaskList";
		} else {
			return "/mnt/nand/TaskList";
		}
	}

	public static String getMediaBasePath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir + File.separator + "MediaStore";
		} else {
			return "/mnt/nand/MediaStore";
		}
	}

	public static String getSyscfgPath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir + File.separator + "playerbsi";
		} else {
			return "/mnt/nand/playerbsi";
		}
	}

	public static String getDataBasePath() {
		return "/data/data/com.bsi.dms/databases";
	}

	public static String getLogPath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir;
		} else {
			return "/mnt/nand";
		}
	}

	public static String getTTSApkPath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir + File.separator + "playerbsi";
		} else {
			return null;
		}
	}

	public static String getTempFilePath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir + File.separator + "temp";
		} else {
			return "/mnt/nand/temp";
		}
	}

	public static String getFontPath() {
		String sdDir = getSDPath();
		if (sdDir != null) {
			return sdDir;
		} else {
			return "/mnt/nand";
		}
	}

	public static void chmodAllMediaFiles() {
		Log.e("wby", "media base" + getMediaBasePath());
		File fileMediaBase = new File(getMediaBasePath());
		CommonUtil.chmodPlayer(fileMediaBase);

		File fileMaterials = new File(getMediaBasePath() + File.separator
				+ "Materials");
		CommonUtil.chmodPlayer(fileMaterials);
		File playerbsi = new File(getTTSApkPath());
		CommonUtil.chmodPlayer(playerbsi);

	}

	public static void clearAllLocalData() {
		Log.w("CommonUtil", "clear all local data");
		// deleteAllDir (getSyscfgPath() );
		deleteAllDir(getTaskBasePath());
		deleteAllDir(getMediaBasePath());
		deleteAllDir(getDataBasePath());
	}

	public static void clearLogData() {
		deleteAllDir(getLogPath() + File.separator + "logs");
	}

	public static void deleteLogData() {
		String logDir = getLogPath() + File.separator + "logs" + File.separator;
		File file = new File(logDir);
		if (!file.exists()) {
			return;
		}
		String lastLog = null;
		String filesList[];
		filesList = file.list();
		if (filesList == null || filesList.length == 0) {
			return;
		}
		for (int i = 0; i < filesList.length; i++) {
			if (lastLog == null) {
				lastLog = filesList[i];
			} else {
				if (lastLog.compareTo(filesList[i]) > 0) {
					deleteAllDir(logDir + filesList[i]);
				} else {
					deleteAllDir(logDir + lastLog);
					lastLog = filesList[i];
				}
			}
		}
		Log.w(TAG, "deleteLogData complete, keep:" + lastLog);
	}

	public static File createFlagFile(String path) {
		String fileFolder = path.substring(0, path.lastIndexOf(File.separator));
		String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
		File filePath = new File(fileFolder);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		File file = new File(fileFolder, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(FLAG_FILE_CONTENT.getBytes());
			fos.flush();
		} catch (IOException e) {
			Log.e(TAG, "create flag file fail file:" + path);
		} finally {
			closeStream(fos);
		}
		if (isKaiboer()) {
			chmodFileAndDir(file, "777");
		}
		return file;
	}

	public static boolean writeTaskXml(String str, String filename)
			throws IOException {
		String path = CommonUtil.getTaskBasePath() + File.separator + filename;
		File file = createFlagFile(path);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] bytes = str.getBytes();
		int b = bytes.length;
		fos.write(bytes, 0, b);
		fos.flush();
		fos.close();
		return true;
	}

	public static boolean writeCfgXml(String str, String filename)
			throws IOException {
		if (str == null || "".equals(str)) {
			return false;
		}
		String path = CommonUtil.getSyscfgPath() + File.separator + filename;
		File file = createFlagFile(path);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] bytes = str.getBytes();
		int b = bytes.length;
		fos.write(bytes, 0, b);
		fos.flush();
		fos.close();
		return true;
	}

	public static String readTaskXmlString(String filename) throws IOException {
		String path = CommonUtil.getTaskBasePath() + File.separator + filename;
		File f1 = new File(path);
		FileInputStream in = new FileInputStream(f1);
		byte[] b = new byte[(int) f1.length()];
		int index = 0;
		int temp = 0;
		while ((temp = in.read()) != -1) {
			b[index] = (byte) temp;
			index++;
		}
		in.close();
		return new String(b);
	}

	public static String genMaskString(String input, char mask) {
		if (input == null || input.length() < 2) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(input.charAt(0));
		for (int i = 0; i < input.length() - 1; i++) {
			sb.append(mask);
		}
		return sb.toString();
	}

	public static boolean deleteDir(File dir) {
		if (dir == null || !dir.exists()) {
			return false;
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}
		}

		return dir.delete();
	}

	public static boolean deleteAllDir(String dirString) {
		File dir = new File(dirString);
		if (!dir.exists()) {
			return false;
		}
		return deleteDir(dir);
	}

	public static boolean isJSExist() {
		return isFlagFileExsit(CommonUtil.getMediaBasePath() + File.separator
				+ "PlayList" + File.separator + "jsCss" + "_"
				+ PlayerConst.FLAG_COMPLETE);
	}

	public static boolean isFlagFileExsit(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		File file = new File(path);
		return file.exists() && file.length() > 0;
	}

	public static void setJSExist() {
		createFlagFile(CommonUtil.getMediaBasePath() + File.separator
				+ "PlayList" + File.separator + "jsCss" + "_"
				+ PlayerConst.FLAG_COMPLETE);
	}

	public static String getUTF8XMLString(String xml) {
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
			System.out.println("utf-8 编码：" + xmlUTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return xmlUTF8;
	}

	public static void chmodPlayer(File destFile) {
		String command = "chmod -R 777 " + destFile.getAbsolutePath();
		Log.e("wby", "command" + command);
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void chmodFileAndDir(File file, String mode) {
		try {
			if (file == null) {
				return;
			}
			if (file.getParentFile() != null) {
				chmodFileAndDir(file.getParentFile(), mode);
			}
			String command = "chmod " + mode + " " + file.getAbsolutePath();
			Log.d("Util", "command " + command);
			Runtime.getRuntime().exec(command).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static long getSDCardPercent() {
		// long[] sdCardInfo=new long[2];
		long percent = 0;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();

			long total = bSize * bCount;// 总大小
			long avail = bSize * availBlocks;// 可用大小
			percent = (total - avail) * 100 / total;
			Log.e("CommonUtil", "total:" + total + "avail:" + avail);
		}
		return percent;
	}

	public static String readCpuUsagePercent() {
		double cpuUsage = CommonUtil.readCpuUsage();
		DecimalFormat percentFormat = new DecimalFormat("##.##%");
		return percentFormat.format(cpuUsage);
	}

	public static double readCpuUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static boolean isKaiboer() {
		if (isFileExist("/mnt/nand")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeamonInstalled() {
		List<PackageInfo> packageInfos = PlayerApplication.getInstance()
				.getPackageManager().getInstalledPackages(0);
		if (packageInfos == null || packageInfos.size() == 0) {
			return false;
		}
		for (PackageInfo info : packageInfos) {
			if (info.packageName.equals("com.bsi.dms.deamon")) {
				return true;
			}
		}
		return false;
	}

	public static void WriteLog(String module, String msg) {
		String time = CommonUtil.getSystemTime();
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		Programtask cur = PlayerController.getInstance().getCurrentPlay();
		String taskno = (cur == null) ? "" : cur.getTaskno();
		if (taskno == null) {
			taskno = "";
		}
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
			String csOk = xmlCreate.createXml("UploadLog", playerid, taskno,
					time + "|" + module + "|" + module + "|" + msg, " ");

			TcpSessionThread.getInstance().sendString(csOk);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) PlayerApplication.getInstance()
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null && (!info.getMacAddress().equals("02:00:00:00:00:00"))) {
			return info.getMacAddress();
		} else {
			return Macutils.getMac();
		}
	}

	public static boolean isHttpFileExist(String path) {
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			if (conn.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
			// e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		// 设置超时时间
	}

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeSocket(Socket s) {
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getInstalledDeamonVersion() {
		PackageManager pm = PlayerApplication.getInstance().getPackageManager();
		List<PackageInfo> pakageinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : pakageinfos) {
			if (info.packageName.equals("com.bsi.dms.deamon")) {
				return info.versionCode;
			}
		}
		return -1;
	}

	public static boolean copyAssertsToSdcard(String srcFile, String destFile) {
		Context context = PlayerApplication.getInstance();
		try {
			AssetManager assets = context.getAssets();
			InputStream stream;
			stream = assets.open(srcFile);
			if (stream == null) {
				Toast.makeText(context, "无法拷贝:" + srcFile, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			File file = new File(destFile);
			if (!ApkInstaller.writeStreamToFile(stream, file)) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void installApk(String destFile) {
		try {
			String command = "pm install -r " + destFile;
			Runtime.getRuntime().exec(command).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void reportCommandAck(String playerId, String taskNo) {
		try {
			XMLTaskCreate xmlCreate = new XMLTaskCreate();
			String csOk = xmlCreate.createXml("ACK_OK", playerId, taskNo, "",
					"");
			TcpSessionThread.getInstance().sendString(csOk);
		} catch (Exception e) {
			Log.e(TAG, "report command ack fail,playerId=" + playerId
					+ " taskNo=" + taskNo);
			e.printStackTrace();
		}
	}
	
	public static boolean hasRootPermission() {
		boolean rooted = true;
		try {
			File su = new File("/system/bin/su");
			if (su.exists() == false) {
				su = new File("/system/xbin/su");
				if (su.exists() == false) {
					rooted = false;
				}
			}
		} catch (Exception e) {
			// log( "Can't obtain root - Here is what I know: "+e.getMessage());
			rooted = false;
		}
		return rooted;
	}
}
