package com.bsi.dms.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.bsi.dms.utils.CommonUtil;


public class LogWriterAndr {
	private static LogWriterAndr INSTANCE = null;
	public static String PATH_LOGCAT;
	public static String LOG_FOLDER;
	private LogDumper mLogDumper = null;
	
	
	public static LogWriterAndr getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LogWriterAndr();
		}
		return INSTANCE;
	}

	public  void init( ){
		LOG_FOLDER = CommonUtil.getLogPath();
		//logPath.append("logs").append("/");
		PATH_LOGCAT = LOG_FOLDER+File.separator+ "logs";

		// 鏃ュ織淇濆瓨鐩綍
		File file = new File(PATH_LOGCAT);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public void start() {
		if (mLogDumper == null) {
			mLogDumper = new LogDumper(PATH_LOGCAT);
			//mLogDumper.setPriority(5);
			mLogDumper.start();
		}
	}
	
	public void stop() {
		if (mLogDumper != null) {
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
	}
	
	
	private class LogDumper extends Thread {
		String filePath;
		private Process playLogcatProc;
		private BufferedReader playReader = null;
		private boolean mRunning = false;
		String playcmds = null;
		private FileOutputStream playOut = null;
		private int year;
		private int month;
		private int day;

		
		
		
		public LogDumper(String file) {

			filePath = file;

			/*
			Calendar c = Calendar.getInstance();
			// 鍙栧緱绯荤粺鏃ユ湡
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);

			File playFile = new File(filePath, "PlayerLog" + getCurrentDate() + ".txt");

			if (!playFile.exists()) {
				try {
					playFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				playOut = new FileOutputStream(playFile, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			*/

			playcmds = "logcat *:W";
		}
		
		public void writeLog(byte[] log){
			Calendar c = Calendar.getInstance();
			// 鍙栧緱绯荤粺鏃ユ湡
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);

			File playFile = new File(filePath, "PlayerLog" + getCurrentDate() + ".txt");

			CommonUtil.ensureFileExist(playFile.getAbsolutePath());

			try {
				playOut = new FileOutputStream(playFile, true);
				playOut.write(log);
				playOut.write("\n".getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public void stopLogs() {
			mRunning = false;
		}

		public String getCurrentDate() {
			String rmonth = "";
			String rday = "";
			if ((month + 1) < 10) {
				rmonth = "0" + (month + 1);
			} else {
				rmonth = "" + (month + 1);
			}
			if (day < 10) {
				rday = "0" + day;
			} else {
				rday = "" + day;
			}
			return year + rmonth + rday;
		}

		@Override
		public void run() {
			mRunning = true;
			try {

				Log.e("log", "log run-----------"  );
				playLogcatProc = Runtime.getRuntime().exec(playcmds);

				playReader = new BufferedReader(new InputStreamReader(
						playLogcatProc.getInputStream()), 1024);

				String pline = null;
				while (mRunning && (pline = playReader.readLine()) != null) {
					if (!mRunning) {
						break;
					}
					if (pline.length() == 0) {
						continue;
					}
					/*
					if (playOut != null) {
						synchronized (playOut) {														
							playOut.write(pline.getBytes());
							playOut.write("\n".getBytes());
							//playOut.flush();												
						}												
					}
					*/
					writeLog( pline.getBytes() );	
					//Thread.sleep(2000);
				}

			} catch (IOException e) {
				e.printStackTrace();
				return;
			} finally {
				if (playLogcatProc != null) {
					playLogcatProc.destroy();
					playLogcatProc = null;
				}
				if (playReader != null) {
					try {
						playReader.close();
						playReader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (playOut != null) {
					try {
						playOut.close();
						playOut = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
}
