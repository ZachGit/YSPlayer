package com.bsi.dms.config;

public class PlayerConst {
	public static final int PLAY_NONE = -1;
	public static final int PLAY_DEFAULT = 3;
	public static final int PLAY_LIVE = 1;
	public static final int PLAY_INS = 2;		

	
	
	public static final int statusPlaying = 1;
	public static final int statusPause = 2;
	public static final int statusStop = 3;
	public static final int statusUnkonwn = 4; 
	public static final int statusOffline = 5;
	
	public static final int priorityChangeScreen = 10;
	public static final int priorityInsert = 9;
	public static final int priorityLive = 8;
	
	public static final String FLAG_COMPLETE = "c2d8db08a641fca5d4932682e11573975d69b991";
	public static final String FLAG_LOOPPLAY = "5fd57da1ae88a383aafde06464a19dac61d856ae";
	public static final String FLAG_ERR = "6caf7d91f69063669c65ac711e1cd012a97e4ded";
	
	public static final int RESIZE_VIDEO = 101;
	public static final int PLAY_CHMOD = 102;		
	public static final int PLAY_VIDEOVIEW_HIDE = 103;	
	public static final int PLAY_VIDEOVIEW_SHOW = 104;	
	public static final int PLAY_RTSP_SHOW = 105;	
	
	public static final int CLIENT_TYPE_X86 = 1;
	public static final int CLIENT_TYPE_ANDROID = 2;
	
	public static final String CMD_VALUE_SPLITOR = "\\|";
}
