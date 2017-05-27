package com.bsi.dms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.VideoView;

import com.bsi.dms.activity.ConfigActivity;
import com.bsi.dms.activity.RequestPlayActivity;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.Programtask;
import com.bsi.dms.bean.Syscfg;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConfig;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.log.LogWriterAndr;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.prompt.NetworkStatusWatcher;
import com.bsi.dms.tts.TTSManager;
import com.bsi.dms.tts.TTSManagerImpl;
import com.bsi.dms.update.UpdateManagerImpl;
import com.bsi.dms.utils.CommonUtil;
import com.bsi.dms.utils.Macutils;
import com.bsi.dms.video.AudioPlayer;
import com.bsi.dms.video.Player;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private WifiManager wifi;
	private WifiInfo info;
	private static PlayerLoopTask loopTask = null;
	private String mac = null;
	private AudioManager audiomanage;

	final int SET_CONFIG = 0x123;
	final int SET_REQUEAT = 0x124;

	public static final int APP_ID = 123;
	private WebView wv;
	private String wvCurUrl;
	private HtmlHandler playHandler;
	private PlayerHtmlTask playerHtmlTask;
	private static Context mContext;
	private Thread showThread;
	static Thread loopThread;

	public SurfaceView surfaceView; 
	private VideoCtr videoCtr;
	private boolean isSurfaceViewInMain = false;

	private AudioCtr audioJS;
	private TTSWebviewInterface mTTSWebviewInterface;
	private LogWriterAndr logWriter;

	private Timer timer; 
	//用于重启应用
	int mHour, mMinute,mSECOND;
	private Handler mHandler = new Handler();
	boolean booleanTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetworkStatusWatcher.getInstance().start();
		mContext = getApplicationContext();
		setContentView(R.layout.activity_main);
		// Log.e(TAG, "start vnc Server --------------");
		// s = new ServerManager();
		// s.startServer();
		getPreferences();
		startApp();
		// 获取屏幕分辨率 宽度*高度*频率
		Display display = getWindowManager().getDefaultDisplay();
		PlayerConfig.setWidth(display.getWidth());
		PlayerConfig.setHeight(display.getHeight());
		PlayerConfig.setRate(Math.round(display.getRefreshRate()));

		// 获取音量
		audiomanage = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		PlayerConfig.setMaxVolume(audiomanage
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // 获取最大音量值
		PlayerConfig.setCurrentVolume(audiomanage
				.getStreamVolume(AudioManager.STREAM_MUSIC)); // 获取当前音量值

		// 设置系统重启
		PlayerConfig.pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// pManager.reboot("");

		Syscfg syscfg = PlayerApplication.getInstance().sysconfig;
		
		if (syscfg != null) {
			mac = syscfg.getMac();
		}
		Log.i(TAG, "mac info:" + mac);
		if (mac == null || "".equals(mac)) {
			wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if (wifi != null) {
				if (!wifi.isWifiEnabled()) {
					wifi.setWifiEnabled(true);
				}
				WifiInfo info = wifi.getConnectionInfo();
				if (info != null) {
					String macAddress = info.getMacAddress(); // 你要的是这个么?
					if (syscfg != null) {
						if(macAddress.equals("02:00:00:00:00:00")){
							macAddress = Macutils.getMac();
							syscfg.setMac(macAddress);
							System.out.println("mac>>>："+macAddress);
						}
					}
				}
			}
		}

		DownloadEngine.getInstance().setContext(MainActivity.this);
		PlayerController.getInstance().startPlay();
		// PlayerController.getInstance().clearChangeScreen();

		surfaceView = new SurfaceView(this);
		audioJS = new AudioCtr();
		wv = (WebView) findViewById(R.id.wv);
		WebSettings webSettings = wv.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setPluginsEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setPluginState(PluginState.ON);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				removeSurfaceView();
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

		});
		wv.setWebChromeClient(chromeClient);
		// wv.setOnKeyListener(new View.OnKeyListener() {
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (event.getAction() == KeyEvent.ACTION_DOWN
		// && event.getRepeatCount() == 0) {
		// switch (keyCode) {
		// case KeyEvent.KEYCODE_2:
		// Intent intcfg = new Intent();
		// intcfg.setClass(MainActivity.this, ConfigActivity.class);
		// startActivity(intcfg);
		// Log.i(TAG, "click key 2");
		// return true;
		// case KeyEvent.KEYCODE_1:
		// case KeyEvent.KEYCODE_3:
		// Intent intreq = new Intent();
		// intreq.setClass(MainActivity.this,
		// RequestPlayActivity.class);
		// startActivity(intreq);
		// Log.i(TAG, "click key 1");
		// return true;
		// default:
		// return false;
		// }
		// } else if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
		// wv.goBack();
		// return true;
		// }
		//
		// return false;
		// }
		//
		// });

		PlayerController.getInstance().setTurndown(false);

		playHandler = new HtmlHandler();
		playerHtmlTask = new PlayerHtmlTask(playHandler);
		showThread = new Thread(playerHtmlTask);
		showThread.start();

		videoCtr = new VideoCtr(this, playHandler, surfaceView);
		wv.addJavascriptInterface(videoCtr, "videobsi");
		wv.addJavascriptInterface(audioJS, "audiobsi");
		mTTSWebviewInterface = new TTSWebviewInterface(this);
		wv.addJavascriptInterface(mTTSWebviewInterface, "ttsbsi");

		playDefault();
		PlayerConfig.view = getWindow().getDecorView();

		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				CommonUtil.deleteLogData();
				logWriter = LogWriterAndr.getInstance();
				logWriter.init();
				logWriter.start();
				if (!CommonUtil.isJSExist()) {
					PlayerApplication.getInstance().copyJsFile();
				}
				try {
					PlayerApplication.getInstance().getSyscfg();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				UpdateManagerImpl.getInstance().updateDeamon();
				startService(new Intent("com.bsi.dms.START_PLAYER_SERVICE"));
			}
		};
		timer.schedule(timerTask, 12000);
		 
		// for kaiboer
		if (CommonUtil.isKaiboer()) {
			CommonUtil.chmodAllMediaFiles();
		
		}  
		
	    wv.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				Intent intcfg = new Intent();
				intcfg.setClass(MainActivity.this, ConfigActivity.class);
				startActivity(intcfg);
				
				return false;
			}
     	
	    });
	}

	public void startApp(){
		 mHandler.post(new Runnable() {
	            @Override
	            public void run()
	            {
	                Calendar mCalendar = Calendar.getInstance();
	                mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
	                mMinute = mCalendar.get(Calendar.MINUTE);
	                mSECOND = mCalendar.get(Calendar.SECOND);
	                Log.i("TAG", mHour + "时" + "  " + mMinute + "分" +mSECOND+"秒"+"当前标签值："+booleanTime);
	                //计算出秒
	                if(mMinute == 2 && !booleanTime){
                        Log.i("TAG", "到判断时间重启 ："+booleanTime );
                        saveSharedPreferences(true);
    	                starApp();
	                }else if(mMinute == 3 && booleanTime){
	                	saveSharedPreferences(false);
	                	Log.i("TAG", "到判断时间重启后 ："+booleanTime );
	                }
	                int i = 1000*60*30;
	                mHandler.postDelayed(this, i);
	            }
	        });
	}
	
	/**
     * 重启应用
     */
    public void starApp() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    /**
     *保存用于重启应用的标签
     */
    private void saveSharedPreferences(boolean booleanTimer){
    	booleanTime = booleanTimer;
    	SharedPreferences preferences=mContext.getSharedPreferences("time", Context.MODE_WORLD_READABLE);  
        Editor editor=preferences.edit();  
        editor.putBoolean("booleanTime", booleanTime);  
        editor.commit(); 
        Log.i(TAG,  " 存储  时" + booleanTime);
        
    }
    /**
     * 提取存储数据
     */
    private void getPreferences(){
    	SharedPreferences preferences=mContext.getSharedPreferences("time", Context.MODE_WORLD_READABLE);
    	booleanTime = preferences.getBoolean("booleanTime", false);
    	Log.i(TAG,  " 提取  时" + booleanTime);
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

	public static void stopLoopTask() {
		if (loopTask != null) {
			loopTask.setStop(true);
			loopThread.interrupt();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		menu.add(0, SET_REQUEAT, 0, "点播节目");
		menu.add(0, SET_CONFIG, 1, "终端配置");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
		case SET_CONFIG:
			Intent intcfg = new Intent();
			intcfg.setClass(MainActivity.this, ConfigActivity.class);
			startActivity(intcfg);
			break;
		case SET_REQUEAT:
			Intent intreq = new Intent();
			intreq.setClass(MainActivity.this, RequestPlayActivity.class);
			startActivity(intreq);
			break;
		default:
			return super.onOptionsItemSelected(mi);
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NetworkStatusWatcher.getInstance().stop();
		Log.i(TAG, "stop mainActivity");
		stop();
		mTTSWebviewInterface.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			super.openOptionsMenu();
		}

		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
			new AlertDialog.Builder(MainActivity.this)
					.setMessage("确定退出程序吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									stop();
									MainActivity.this.finish();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									return;
								}

							}).show();
			return true;
		}
		if (event.getRepeatCount() == 0) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_1:
				Intent intreq = new Intent();
				intreq.setClass(MainActivity.this, RequestPlayActivity.class);
				startActivity(intreq);
				Log.i(TAG, "click key 1");
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void stop() {
		// Intent playerIntent = new Intent("com.bsi.dms.START_PLAYER_SERVICE");
		// stopService(playerIntent);
		if (showThread != null) {
			showThread.interrupt();
		}
		stopService(new Intent("com.bsi.dms.START_PLAYER_SERVICE"));

	}

	public String getLocalMacAddress() {
		return info.getMacAddress().toUpperCase();
	}

	public String getHardwareId() {
		return info.getMacAddress().replaceAll(":", "").toUpperCase();
	}

	public String getLocalIp() {
		int ipAddress = info.getIpAddress();
		String ip = CommonUtil.intToIp(ipAddress);
		return ip;
	}

	private WebChromeClient chromeClient = new WebChromeClient() {
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
		}
	};

	private void playDefault() {
		if (wv == null) {
			wv = (WebView) findViewById(R.id.wv);
		}
		String url = "file:///android_asset/index.html";
		if ((url.startsWith("http://") || url.startsWith("file://"))
				&& !url.startsWith("file:///android_asset")) {
			setWebViewCurUrl(url);
		} else {
			setWebViewCurUrl(null);
		}
		wv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

		wv.loadUrl(url);
		PlayerController.getInstance().setDefaultPlay(true);
	}

	private void setWebViewCurUrl(String url) {
		wvCurUrl = null;
		if (url != null) {
			wvCurUrl = new String(url);
		}
	}

	private String getWebViewBasePath() {
		if (wvCurUrl != null) {
			String fileFolder = wvCurUrl.substring(0,
					wvCurUrl.lastIndexOf(File.separator));
			if (fileFolder != null && fileFolder.startsWith("file://")) {
				fileFolder = fileFolder.replace("file://", "");
			}
			return fileFolder;
		} else {
			return null;
		}

	}

	class HtmlHandler extends Handler {
		// private PlayerLoopTask loopTask = null;
		private int surfaceHeight = 0;
		private int surfaceWidth = 0;

		private void playProgram(Programtask pro) {
			// video
			// pro.getProgramVideoFromXml();
			// Log.e(TAG, "video play:" + pro.getProgramvideo() );

			// String url = pro.getUrl();
			if (pro != null) {
				audioJS.stopAudio();
			}
			if (pro.isLoopProgram()) {
				// play loop program
				Log.w(TAG, "play loop program ------------");
				Playlist loop = PlayerController.getInstance()
						.findLoopPlayList(pro.getProgramid());
				if (loop == null) {
					Log.e(TAG, "loop is null");
					return;
				}

				if (PlayerController.getInstance().isDefaultPlay()) {
					PlayerController.getInstance().setDefaultPlay(false);
				}
				pro.setPlayed(true);

				if (loopTask != null && pro != null) {
					loopTask.setStop(true);
					PlayerController.getInstance().setAllLoopPlayed(false);
					loopThread.interrupt();
				}

				loopTask = new PlayerLoopTask(playHandler, loop);
				loopThread = new Thread(loopTask);
				loopThread.start();
			} else {
				if (!PlayerController.getInstance().isInLoopList(
						pro.getProgramid())) {
					if (loopTask != null && pro != null) {
						loopTask.setStop(true);
						PlayerController.getInstance().setAllLoopPlayed(false);
						loopThread.interrupt();
					}
				}
				String url = pro.getPlayUrl();
				if (wv == null) {
					wv = (WebView) findViewById(R.id.wv);
				}
				if (PlayerController.getInstance().isDefaultPlay()) {
					PlayerController.getInstance().setDefaultPlay(false);
				}
				pro.setPlayed(true);
				wv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
				wv.loadUrl(url);
				Log.e(TAG, "playing-----------" + url);
				setWebViewCurUrl(url);
			}
		}

		private void hideSurface() {
			LayoutParams lpSurface = surfaceView.getLayoutParams();
			surfaceWidth = lpSurface.width;
			surfaceHeight = lpSurface.height;
			lpSurface.width = 1;
			lpSurface.height = 1;
			surfaceView.setLayoutParams(lpSurface);
			surfaceView.invalidate();
		}

		private void showSurface() {
			if (surfaceWidth != 0 && surfaceHeight != 0) {
				LayoutParams lp = surfaceView.getLayoutParams();
				lp.width = surfaceWidth;
				lp.height = surfaceHeight;
				surfaceView.setLayoutParams(lp);
				surfaceView.invalidate();
			}
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case PlayerConst.PLAY_LIVE:
				removeSurfaceView();
				Programtask p = (Programtask) msg.getData().get("program");
				playProgram(p);
				break;
			case PlayerConst.RESIZE_VIDEO:
				int width = bundle.getInt("width");
				int height = bundle.getInt("height");
				int x = bundle.getInt("x");
				int y = bundle.getInt("y");
				AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(
						width, height, x, y);
				if (surfaceView != null) {
					surfaceView.setLayoutParams(param);
				}

				// MainActivity.this.addContentView(surfaceView, param);
				addSurfaceView();
				break; 
			case PlayerConst.PLAY_DEFAULT:
				removeSurfaceView();
				playDefault();
				break;
			case PlayerConst.PLAY_VIDEOVIEW_HIDE:
				hideSurface();
				break;
			case PlayerConst.PLAY_VIDEOVIEW_SHOW:
				showSurface();
				break;

			}
		}

	}

	public void removeSurfaceView() {
		if (isSurfaceViewInMain == true) {
			ViewGroup parent = (ViewGroup) wv.getParent();
			parent.removeView(surfaceView);
			videoCtr.stopPlay();
			isSurfaceViewInMain = false;
		}
	}

	public void addSurfaceView() {
		if (isSurfaceViewInMain == false) {
			ViewGroup parent = (ViewGroup) wv.getParent();
			parent.addView(surfaceView);
			isSurfaceViewInMain = true;
		}
	}

	public void refreshProgram(Programtask target) {
		if (target != null && playHandler != null) {
			Log.i(TAG, "start new url:" + target.getUrl());
			Message message = Message.obtain();
			message.what = PlayerConst.PLAY_LIVE;
			Bundle b = new Bundle();
			b.putSerializable("program", target);
			message.setData(b);
			playHandler.sendMessage(message);
			Log.i(TAG, "playing ----------" + target.getUrl());
		}
	}

	class PlayerHtmlTask implements Runnable {
		Handler handler;

		private Programtask target = null;

		public PlayerHtmlTask(Handler handler) {
			// super();
			this.handler = handler;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (PlayerController.getInstance().isTurndown()) {
					Log.e(TAG, "shut down #######################");

					Intent newIntent = new Intent(Intent.ACTION_SHUTDOWN);
					newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(newIntent);

					/*
					 * Intent intent = new
					 * Intent(Intent.ACTION_REQUEST_SHUTDOWN);
					 * intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
					 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * mContext.startActivity(intent);
					 */
				}

				Log.w(TAG, "show html thead run");
				if (PlayerController.getInstance().getPlayStatus() == PlayerController.statusStop) {
					if (PlayerController.getInstance().isDefaultPlay()) {
						continue;
					} else {
						// playDefault();
						Message message = Message.obtain();
						message.what = PlayerConst.PLAY_DEFAULT;
						handler.sendMessage(message);
						continue;
					}
				}

				target = PlayerController.getInstance().getRunProgram();
				/*
				 * if(target != null &&
				 * "localfirst".equals(target.getOnlinemode() ) &&
				 * target.isDownloadComplete() ){ Message message =
				 * Message.obtain(); message.what = PlayerConst.PLAY_CHMOD;
				 * //Integer.valueOf(item.getType());
				 * handler.sendMessage(message); }
				 */

				if (target != null) {
					Log.i(TAG, "start new url:" + target.getUrl());
					Message message = Message.obtain();
					message.what = PlayerConst.PLAY_LIVE;
					Bundle b = new Bundle();
					b.putSerializable("program", target);
					message.setData(b);
					handler.sendMessage(message);
					Log.i(TAG, "playing ----------" + target.getUrl());
				} else {
					if (PlayerController.getInstance().isDownloadRefresh()) {
						Programtask cur = PlayerController.getInstance()
								.getCurrentPlay();

						if (cur != null
								&& ("localfirst".equals(cur.getOnlinemode()) || "local"
										.equals(cur.getOnlinemode()))) {
							refreshProgram(cur);
						}
						PlayerController.getInstance()
								.setDownloadRefresh(false);
					}
				}

			}

		}

	}

	class PlayerLoopTask implements Runnable {
		Handler handler;
		Playlist playlist;
		private boolean stop = false;

		public PlayerLoopTask(Handler handler, Playlist playlist) {
			super();
			this.handler = handler;
			this.playlist = playlist;
		}

		@Override
		public void run() {
			Log.w(TAG, "PlayerLoopTask thread start");
			while (!isStop()) {
				if (playlist == null) {
					break;
				}
				List<Programtask> protasks = playlist.getProgramtasks();
				for (Programtask program : protasks) {
					// play
					if (isStop()) {
						return;
					}
					Log.w(TAG, "loop play url---------- ");
					Message message = Message.obtain();
					message.what = PlayerConst.PLAY_LIVE;
					Bundle b = new Bundle();
					b.putSerializable("program", program);
					message.setData(b);
					this.handler.sendMessage(message);

					int sleep = Integer.parseInt(program.getTime()) * 1000;
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}

		public boolean isStop() {
			return stop;
		}

		public void setStop(boolean stop) {
			Log.e(TAG, "set loop stop----------");
			this.stop = stop;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "change activity : stop play");
		videoCtr.stopPlay();
		audioJS.stopAudio();
		PlayerController.getInstance().stopPlay();
		// wv.pauseTimers();
		// wv.stopLoading();
		// wv.loadData("<a></a>", "text/html", "utf-8");
		// wv.loadDataWithBaseURL(null, "","text/html", "utf-8",null);
		// wv.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "change activity : start play"); 
		if ( PlayerController.getInstance().getPlayStatus()== PlayerController.statusStop) {
			PlayerController.getInstance().startPlay();
		}
		 //wv.onResume();
	}

	private class VideoCtr {
		private Activity activity;
		private Player player;
		private SurfaceView surfaceVideo;
		Handler handler;

		public VideoCtr(Activity activity, Handler handler,
				SurfaceView surfaceView) {
			this.activity = activity;
			// surfaceVideo = (SurfaceView)
			// this.activity.findViewById(R.id.surfaceViewVideo);
			surfaceVideo = surfaceView;
			this.handler = handler;
			player = new Player(surfaceVideo, handler);
		}

		private boolean isValidateVideo(String videoUrl) {
			String path = videoUrl;

			if (!path.startsWith("http://") && !path.startsWith("/")) {
				path = getWebViewBasePath() + File.separator + videoUrl;
			}
			if (path.startsWith("http://")) {
				// player.playUrl(videoUrl);
				if (CommonUtil.isHttpFileExist(path)) {
					return true;
				} else {
					return false;
				}
			} else if (path.startsWith("/")) {
				if (CommonUtil.isFileExist(path)) {
					return true;
				} else {
					return false;
				}
			}

			return true;
		}
 
		
		@JavascriptInterface
		public void playUrl(String videoUrl, int width, int height, int x, int y) {
			Log.e(TAG, "video width,height,x, y" + width + ',' + height + ','
					+ x + ',' + y);
			// conflict with audio
			audioJS.stopAudio();
			/*
			 * if( !isValidateVideo(videoUrl) ){ Log.e(TAG,
			 * videoUrl+" is not exist"); return; } else{ Log.w(TAG,
			 * videoUrl+"  exist---------"); }
			 */ 		
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			 
			int wd=size.x;
			int ht=size.y;
			
			/*
			//按比例缩放视频
			double w=width;
			double h=height;
			double l=x;
			double t=y;
			double fscale=1;
			
			fscale=w/1280;
			width=(int)(fscale*wd);
			
			fscale=(h/720);
			height=(int)(fscale*ht);
			height+=30;
			
			fscale=l/1280;
			x=(int)(fscale*wd);
			
			fscale=	t/720;
			y=(int)(fscale*ht);	 
			
			y+=20;
			*/
			
			changeSurfaceViewParams(width, height, x, y);
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// player.playUrl(videoUrl);
			// add address
			Log.e(TAG, "play video :" + videoUrl);
			if (videoUrl.startsWith("rtsp://") || videoUrl.startsWith("/")) {
				player.playRtsp(videoUrl);
			}
			else if (videoUrl.startsWith("http://") || videoUrl.startsWith("/")) {
				player.playUrl(videoUrl);
			} else {
				String path = getWebViewBasePath();
				Log.e(TAG, "video play path file:" + path + File.separator
						+ videoUrl);
				player.playUrl(path + File.separator + videoUrl);
			}

		}

		@JavascriptInterface
		public void playMultiUrl(String videoMultiUrl, int num, int width,
				int height, int x, int y) {
			Log.e(TAG, "playMultiUrl video width,height,x, y" + width + ','
					+ height + ',' + x + ',' + y);
			// conflict with audio
			audioJS.stopAudio();

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			 
			int wd=size.x;
			int ht=size.y;
			
			/*
			//按比例缩放视频
			double w=width;
			double h=height;
			double l=x;
			double t=y;
			double fscale=1;
			
			fscale=w/1280;
			width=(int)(fscale*wd);
			
			fscale=(h/720);
			height=(int)(fscale*ht);
			
			fscale=l/1280;
			x=(int)(fscale*wd);
			
			fscale=	t/720;
			y=(int)(fscale*ht);	
			*/			
			
			changeSurfaceViewParams(width, height, x, y);
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// player.playUrl(videoUrl);
			// add address
			Log.e(TAG, "play video :" + videoMultiUrl);
			if (num > 100) {
				num = 100;
			}
			if (videoMultiUrl.startsWith("http://")
					|| videoMultiUrl.startsWith("/")) {
				player.setMultiUrl(videoMultiUrl, num, ";");
				player.playMultiUrl();
			} else {
				String path = getWebViewBasePath();
				// Log.e(TAG, "video play path file:" + path + File.separator +
				// videoMultiUrl);
				// player.playUrl(path + File.separator + videoMultiUrl);
				String[] strArray = new String[10];
				strArray = videoMultiUrl.split(";");
				String allVideos = path + File.separator + strArray[0];
				for (int i = 1; i < num; i++) {
					allVideos += ";" + path + File.separator + strArray[i];
				}

				Log.e(TAG, "video play path file:" + allVideos);
				player.setMultiUrl(allVideos, num, ";");
				player.playMultiUrl();
			}

		}

		@JavascriptInterface
		public void stopPlay() {
			if (player != null) {
				player.stop();
			}
		}

		private void changeSurfaceViewParams(int width, int height, int x, int y) {
			Message message = Message.obtain();
			message.what = PlayerConst.RESIZE_VIDEO; // Integer.valueOf(item.getType());
			Bundle b = new Bundle();

			// b.putSerializable("program", target);
			b.putInt("width", width);
			b.putInt("height", height);
			b.putInt("x", x);
			b.putInt("y", y);
			message.setData(b);

			handler.sendMessage(message);
			Log.w(TAG, "resize surfaceView size");
		}
	} // end VideoCtr

	private class AudioCtr {

		AudioPlayer audioCur = null;

		@JavascriptInterface
		public void playUrl(String audioUrl) {
			if (audioCur != null) {
				audioCur.pause();
				audioCur.stop();
			}

			AudioPlayer audio = new AudioPlayer();
			audioCur = audio;
			// audio.playUrl(audioUrl);
			if (audioUrl.startsWith("http://") || audioUrl.startsWith("/")) {
				audio.playUrl(audioUrl);
			} else {
				String path = getWebViewBasePath();
				Log.e(TAG, "audip play path file:" + path + File.separator
						+ audioUrl);
				audio.playUrl(path + File.separator + audioUrl);
			}
		}

		@JavascriptInterface
		public void playLoopUrl(String audioUrl) {
			if (audioCur != null) {
				audioCur.pause();
				audioCur.stop();
			}

			AudioPlayer audio = new AudioPlayer();
			audioCur = audio;
			// audio.playUrl(audioUrl);
			if (audioUrl.startsWith("http://") || audioUrl.startsWith("/")) {
				audio.playLoopUrl(audioUrl);
			} else {
				String path = getWebViewBasePath();
				Log.e(TAG, "audip play path file:" + path + File.separator
						+ audioUrl);
				audio.playLoopUrl(path + File.separator + audioUrl);
			}
		}

		@JavascriptInterface
		public void stopAudio() {
			if (audioCur != null) {
				audioCur.pause();
				audioCur.stop();
				audioCur = null;
			}
		}
	}
	
	public static String loadFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	public String getMacAddress() {
		try {
			return loadFileAsString("/sys/class/net/eth0/address")
					.toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class TTSWebviewInterface {
		private Context mContext;
		private TTSManager mTTSManager;

		public TTSWebviewInterface(Context c) {
			mContext = c;
			mTTSManager = TTSManagerImpl.getInstance(c);
			mTTSManager.setEngine(TTSManager.ENGINE_LOCAL);
		}

		public void stop() {
			mTTSManager.stop();
		}

		public void speak(String text) {
			mTTSManager.speak(text);
		}

		public void onDestroy() {
			mTTSManager.onDestroy();
		}
	}
}
