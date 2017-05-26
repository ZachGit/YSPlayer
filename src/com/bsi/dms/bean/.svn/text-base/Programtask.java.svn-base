package com.bsi.dms.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.text.TextUtils;
import android.util.Log;

import com.bsi.dms.R;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.parse.VideoHandler;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.prompt.PromptManager;
import com.bsi.dms.utils.CommonUtil;

public class Programtask implements Serializable {
	private static final String TAG = "Programtask";
	private String programid;
	private String programname;
	private String onlinemode;

	private String startdate;
	private String enddate;
	private String starttime;
	private String endtime;
	private String week;
	private String cycleindex;
	private String time;
	private String url;
	private String localprotocol;
	private String localftpserver;
	private String localprogramurl;
	private List<String> localres;

	private boolean played;

	private String commtype;
	private String playerid;
	private String taskno;

	private String playlistid;
	private String playlistname;
	private String siteid;

	private int status;
	private int priority = 0;
	private boolean inDownloading = false;

	private ProgramVideo programvideo = null;

	public Programtask() {
		played = false;
		this.status = PlayerConst.statusStop;
		this.inDownloading = false;

	}

	public Programtask(String programid, String programname, String startdate,
			String enddate, String starttime, String endtime, String week,
			String cycleindex, String time, String url) {

		this.programid = programid;
		this.programname = programname;

		this.startdate = startdate;
		this.enddate = enddate;
		this.starttime = starttime;
		this.endtime = endtime;
		this.week = week;
		this.cycleindex = cycleindex;
		this.time = time;
		this.url = url;
		this.played = false;
		this.status = PlayerConst.statusStop;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCommtype() {
		return commtype;
	}

	public void setCommtype(String commtype) {
		this.commtype = commtype;
	}

	public String getPlayerid() {
		return playerid;
	}

	public void setPlayerid(String playerid) {
		this.playerid = playerid;
	}

	public String getTaskno() {
		return taskno;
	}

	public void setTaskno(String taskno) {
		this.taskno = taskno;
	}

	public String getPlaylistid() {
		return playlistid;
	}

	public void setPlaylistid(String playlistid) {
		this.playlistid = playlistid;
	}

	public String getPlaylistname() {
		return playlistname;
	}

	public void setPlaylistname(String playlistname) {
		this.playlistname = playlistname;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getProgramid() {
		return programid;
	}

	public void setProgramid(String programid) {
		this.programid = programid;
	}

	public String getProgramname() {
		return programname;
	}

	public void setProgramname(String programname) {
		this.programname = programname;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getCycleindex() {
		return cycleindex;
	}

	public void setCycleindex(String cycleindex) {
		this.cycleindex = cycleindex;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public boolean getPlayed() {
		return played;
	}

	public void addCommandParam(String commtype, String playerid, String taskno) {
		this.commtype = commtype;
		this.playerid = playerid;
		this.taskno = taskno;
	}

	public void addPlaylistParam(String playlistid, String playlistname,
			String siteid) {
		this.playlistid = playlistid;
		this.playlistname = playlistname;
		this.siteid = siteid;
	}

	public void addPlaylistDate(String loopstartdate, String loopenddate,
			String loopstarttime, String loopendtime, String loopweek) {
		this.startdate = loopstartdate;
		this.enddate = loopenddate;
		this.starttime = loopstarttime;
		this.endtime = loopendtime;
		this.week = loopweek;
	}

	public int getPlayMsgType() {
		return 0;
	}

	public boolean canProgramRun() {
		if (this.isInDateTime() && this.isInWeek()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isInWeek() {
		int index = -1;
		Integer cal = -1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (this.week == null) {
			return false;
		}
		cal = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		// System.out.println(week.toString());
		String strWeek = new String(cal.toString());
		index = this.week.indexOf(strWeek);
		if (index == -1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isInDateTime() {
		int i = -1;
		if (this.getStartdate() == null || this.getStarttime() == null
				|| this.getEnddate() == null || this.getEndtime() == null) {
			return false;
		}

		String startTime = this.getStartdate() + " " + this.getStarttime();
		String endTime = this.getEnddate() + " " + this.getEndtime();

		Date now = new Date();
		String nowDate = CommonUtil.getDate();
		// Log.i("Programtask", nowDate);
		i = CommonUtil.stringToDate(startTime).compareTo(now);
		if (i > 0) { // start end now
			return false;
		}
		i = CommonUtil.stringToDate(endTime).compareTo(now);
		if (i < 0) {
			return false;
		}

		String startDaytime = nowDate + " " + this.getStarttime();
		String endDayTime = nowDate + " " + this.getEndtime();
		i = CommonUtil.stringToDate(startDaytime).compareTo(now);
		if (i > 0) {
			return false;
		}
		i = CommonUtil.stringToDate(endDayTime).compareTo(now);
		if (i < 0) {
			return false;
		}
		return true;
	}

	public boolean canProgramRemove() {
		int i = -1;
		if (this.getEnddate() == null || this.getEndtime() == null) {
			return true;
		}
		String startTime = this.getStartdate() + " " + this.getStarttime();
		String endTime = this.getEnddate() + " " + this.getEndtime();

		Date now = new Date();
		String nowDate = CommonUtil.getDate();
		i = CommonUtil.stringToDate(endTime).compareTo(now);
		if (i < 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAfterChangeScreen() {
		Date now = new Date();
		String nowDate = CommonUtil.getDate();
		String startDaytime = nowDate + " " + this.getStarttime();
		Date schScrDate = PlayerController.getInstance().getChgScrDate();
		if (schScrDate != null
				&& CommonUtil.stringToDate(startDaytime).compareTo(schScrDate) < 0) {
			return false;
		}
		return true;
	}

	public int comparePriority(Programtask p2) {
		return this.priority - p2.getPriority();
	}

	public String getOnlinemode() {
		return onlinemode;
	}

	public void setOnlinemode(String onlinemode) {
		this.onlinemode = onlinemode;
	}

	public String getLocalprotocol() {
		return localprotocol;
	}

	public void setLocalprotocol(String localprotocol) {
		this.localprotocol = localprotocol;
	}

	public String getLocalftpserver() {
		return localftpserver;
	}

	public void setLocalftpserver(String localftpserver) {
		this.localftpserver = localftpserver;
	}

	public String getLocalprogramurl() {
		return localprogramurl;
	}

	public void setLocalprogramurl(String localprogramurl) {
		this.localprogramurl = localprogramurl;
	}

	public List<String> getLocalres() {
		return localres;
	}

	public void setLocalres(List<String> localres) {
		this.localres = localres;
	}

	public ProgramVideo getProgramvideo() {
		return programvideo;
	}

	public void setProgramvideo(ProgramVideo programvideo) {
		this.programvideo = programvideo;
	}

	public void getProgramVideoFromXml() {
		String localfilepath = CommonUtil.getMediaBasePath() + File.separator
				+ "PlayList" + File.separator + this.getPlaylistid()
				+ File.separator + this.getProgramid() + ".xml";
		File localfile = new File(localfilepath);
		if (!localfile.exists()) {
			this.setProgramvideo(null);
			return;
		}

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(localfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		VideoHandler pHandler = new VideoHandler();

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = spf.newSAXParser();
			saxParser.parse(inputStream, pHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProgramVideo video = pHandler.getTarget();
		this.setProgramvideo(video);
	}

	public boolean isNeedDownload() {
		if ("localfirst".equals(this.onlinemode)
				|| "onlinefirst".equals(this.onlinemode)
				|| "local".equals(this.onlinemode)) {
			if (this.isDownloadComplete()) {
				return false;
			} else if (this.isDownloadErr()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void downloadLocal() {
		boolean downRet = false;
		Log.e("wby", "download running");
		if (this.isDownloadComplete()) {
			Log.w(TAG, "download already complete,id=" + programid);
			return;
		}
		this.inDownloading = true;
		if ("localfirst".equals(this.onlinemode)
				|| "onlinefirst".equals(this.onlinemode)
				|| "local".equals(this.onlinemode)) {
			if ("http".equals(this.localprotocol)) {
				String baseurl = PlayerApplication.getInstance().sysconfig
						.getBaseurl();
				String localplaydir = CommonUtil.getMediaBasePath()
						+ File.separator + "PlayList" + File.separator
						+ this.getPlaylistid();
				String localResdir = CommonUtil.getMediaBasePath();

				String remotefile = baseurl + File.separator
						+ this.getLocalprogramurl();
				Log.i("Programtask", "downloading file:" + remotefile);
				File save = new File(localplaydir);
				if (!isDownloadComplete()) {
					if (this.getLocalprogramurl() != null) {
						downRet = DownloadEngine.getInstance().download(
								remotefile, save);
					}

					// download resource
					if (downRet && localres != null && localres.size() != 0) {
						for (String item : localres) {
							String remoteresurl = baseurl + File.separator
									+ item;
							File resFile = new File(localResdir
									+ File.separator + "Materials");
							if (!isResDownloadComplete(localResdir
									+ File.separator + item)) {
								downRet = DownloadEngine.getInstance()
										.download(remoteresurl, resFile);								
								if (downRet == true
										&& isMediaFile(localResdir
												+ File.separator + item)) {
									setMediaComplete(localResdir
											+ File.separator + item);
								}
							} else {								
								downRet = true;
							}
							Log.w(TAG, "downRet==" + downRet+"  file:" + remoteresurl);
							if (downRet == false) {
								break;
							}
						}
					}
					// sync download complete
					if (downRet == true) {
						CommonUtil.createFlagFile(getDownloadCompleteFlag());
						PromptManager.getInstance().toast(
								R.string.downloadComplete,
								PromptManager.ID_NETWORK);
						PlayerController.getInstance().setDownloadRefresh(true);
					} else {
						PromptManager.getInstance().toast(
								R.string.downloadError,
								PromptManager.ID_NETWORK);
						CommonUtil.createFlagFile(getDownloadErrFlag());
					}
					// CommonUtil.chmodAllMediaFiles();
				}
			}

		} else if ("ftp".equals(this.localprotocol)) {

		}
		this.inDownloading = false;
	}

	/*
	 * public boolean isResDownloadComplete(String resfile){ File local = new
	 * File(resfile); if(local.exists()){ return true; } else{ return false; } }
	 */

	public boolean isResDownloadComplete(String resfile) {
		if (isMediaFile(resfile)) {
			return isMediaComplete(resfile);
		} else {
			return false;
		}
	}

	public void setMediaComplete(String res) {
		CommonUtil.createFlagFile(res + "_" + PlayerConst.FLAG_COMPLETE);
	}

	public boolean isMediaComplete(String res) {
		return CommonUtil.isFlagFileExsit(res + "_" + PlayerConst.FLAG_COMPLETE);
	}

	public boolean isMediaFile(String filename) {
		if(TextUtils.isEmpty(filename)){
			return false;
		}
		filename = filename.toLowerCase();
		if (filename.endsWith(".mp4") || filename.endsWith(".wmv")
				|| filename.endsWith(".mp3") || filename.endsWith(".avi") || filename.endsWith(".jpg") || filename.endsWith(".png") ) {
			return true;
		} else {
			return false;
		}
	}

	// MediaStore/PlayList/9/programid_complete
	public String getDownloadCompleteFlag() {
		return CommonUtil.getMediaBasePath() + File.separator + "PlayList"
				+ File.separator + this.getPlaylistid() + File.separator
				+ this.getProgramid() + "_" + PlayerConst.FLAG_COMPLETE;
	}

	public String getDownloadErrFlag() {
		return CommonUtil.getMediaBasePath() + File.separator + "PlayList"
				+ File.separator + this.getPlaylistid() + File.separator
				+ this.getProgramid() + "_" + PlayerConst.FLAG_ERR;
	}

	public boolean isDownloadComplete() {
		return CommonUtil.isFlagFileExsit(getDownloadCompleteFlag());
	}

	public boolean isDownloadErr() {
		File file = new File(getDownloadErrFlag());
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void removeDownloadErr() {
		File file = new File(getDownloadErrFlag());
		if (file.exists()) {
			CommonUtil.deleteDir(file);
		}
	}

	public String getPlayUrl() {
		boolean net = PlayerApplication.getInstance().isNetworkAvailable();
		boolean online = TcpSessionThread.getInstance().isOnline();
		Log.w(TAG, "onlinemode=" + onlinemode + " isDownloadComplete="
				+ isDownloadComplete() + " isDownloadError=" + isDownloadErr());
		if ("localfirst".equals(this.onlinemode)) {
			if (this.isDownloadComplete()) {
				return "file://" + CommonUtil.getMediaBasePath()
						+ File.separator + this.getLocalprogramurl();
			} else {
				if (this.isDownloadErr()) {
					this.removeDownloadErr();
				}
				return this.getUrl();
			}
		} else if ("onlinefirst".equals(this.onlinemode)) {
			if (net && online) {
				return this.getUrl();
			} else {
				if (this.isDownloadErr()) {
					this.removeDownloadErr();
				}
				return "file://" + CommonUtil.getMediaBasePath()
						+ File.separator + this.getLocalprogramurl();
			}

		} else if ("local".equals(this.onlinemode)) {
			// return "file://" + CommonUtil.getMediaBasePath() + File.separator
			// + this.getLocalprogramurl();
			/*
			 * String localfile = CommonUtil.getMediaBasePath() + File.separator
			 * + this.getLocalprogramurl(); if(CommonUtil.isFileExist(localfile)
			 * && resAllExist() ){ return "file://" + localfile; } else{ return
			 * this.getUrl(); }
			 */
			if (this.isDownloadComplete()) {
				return "file://" + CommonUtil.getMediaBasePath()
						+ File.separator + this.getLocalprogramurl();
			} else {
				if (this.isDownloadErr()) {
					this.removeDownloadErr();
				}
				return this.getUrl();
			}

		} else {
			return this.getUrl();
		}
	}

	public boolean resAllExist() {
		List<String> localres = getLocalres();
		if (localres != null && localres.size() != 0) {
			for (String item : localres) {
				if (!CommonUtil.isFileExist(CommonUtil.getMediaBasePath()
						+ File.separator + item)) {
					return false;
				}
			}
		}
		return true;
	}

	public String getVideoBase(String videofile) {
		if ("localfirst".equals(this.onlinemode) && this.isDownloadComplete()) {
			return CommonUtil.getMediaBasePath() + File.separator + videofile;
		} else {
			String baseurl = PlayerApplication.getInstance().sysconfig
					.getBaseurl();
			return baseurl + File.separator + videofile;
		}
	}

	public boolean isLoopProgram() {
		if (this.programid.contains(PlayerConst.FLAG_LOOPPLAY)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean overDate() {
		int i = -1;
		if (this.getStartdate() == null || this.getStarttime() == null
				|| this.getEnddate() == null || this.getEndtime() == null) {
			return true;
		}

		String endTime = this.getEnddate() + " " + this.getEndtime();

		Date now = new Date();

		i = CommonUtil.stringToDate(endTime).compareTo(now);
		if (i < 0) { // end now
			return true;
		}

		return false;
	}

	public boolean canDelete() {
		if (this.overDate() && !this.isInDownloading()) {
			return true;
		}
		return false;
	}

	public boolean isInDownloading() {
		return inDownloading;
	}

	public void setInDownloading(boolean inDownloading) {
		this.inDownloading = inDownloading;
	}
}
