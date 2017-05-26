package com.bsi.dms.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.bsi.dms.MainActivity;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.Programtask;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.utils.CommonUtil;

public class PlayerController {
	public static final String  TAG = "PlayerController";
	public static final String  insPlayer = "InterPlay";
	public static final String  livePlayer = "LivePlay";
	public static final int statusPlaying = 1;
	public static final int statusStop = 2;
	
	private static PlayerController playController;
	private static Programtask currentPlay = null;
	private static List<Programtask> programtasks = null;
	private static List<Programtask>  insProgramtasks = null;
	private List<Playlist>  loopplaylists = null;
	private  Programtask chgScrProgram = null;
	private  Programtask conflictProgram = null;
	private  Date    chgScrDate = null;
	private  int playStatus = 1;		
	private  boolean  defaultPlay = true;
	//turn down command
	private  boolean  turndown = false;
	private  boolean  downloadRefresh = false;
			
	
	static {
		playController = new PlayerController();
	}
	public static PlayerController getInstance(){
		return playController;
	}
	
	public Programtask getCurrentPlay() {
		return currentPlay;
	}

	public synchronized void setCurrentPlay(Programtask currentPlay) {
		this.currentPlay = currentPlay;
	}
	
	public  Programtask getChgScrProgram() {
		return this.chgScrProgram;
	}

	public  void setChgScrProgram(Programtask chgScrProgram) {
		this.chgScrProgram = chgScrProgram;
	}

	public Programtask getConflictProgram() {
		return conflictProgram;
	}

	public void setConflictProgram(Programtask conflictProgram) {
		this.conflictProgram = conflictProgram;
	}

	public Date getChgScrDate() {
		return chgScrDate;
	}

	public void setChgScrDate(Date chgScrDate) {
		this.chgScrDate = chgScrDate;
	}

	public PlayerController(){
		programtasks = new ArrayList<Programtask>();
		insProgramtasks = new  ArrayList<Programtask>();
		loopplaylists = new ArrayList<Playlist>();
	}
	
	public synchronized void addProgram(Programtask program){
		deletePorgram(program.getProgramid() );
		if (insPlayer.equals(program.getCommtype()) ){
			program.setPriority(PlayerConst.priorityInsert);
			insProgramtasks.add(program);	
		}	
		else{
			program.setPriority(PlayerConst.priorityLive);
			programtasks.add(program);
		}
	}
	
	public synchronized void deletePorgram(String programid){
		/*
		for (Programtask info : programtasks) {
			if(info.getProgramid().equals(programid)){
				programtasks.remove(info);
				return ;
			}			
		}
		for (Programtask insItem : insProgramtasks) {
			if(insItem.getProgramid().equals(programid)){
				insProgramtasks.remove(insItem);
			}			
		}
		*/
		List<Programtask> delList = new ArrayList<Programtask>();
		for (Programtask info : programtasks) {
			if(info.getProgramid().equals(programid)){				
				delList.add(info);
			}			
		}
		
		if(delList.size() != 0){
			programtasks.removeAll(delList);
			return;
		}
		
		for (Programtask insItem : insProgramtasks) {
			if(insItem.getProgramid().equals(programid)){
				//insProgramtasks.remove(insItem);
				delList.add(insItem);
			}			
		}	
		if(delList.size() != 0){
			insProgramtasks.removeAll(delList);
			return;
		}
	}
	
	public synchronized boolean canDeleteResItem(String resItem){
		int resNum = 0;
		for (Programtask info : programtasks) {		
			List<String> localres = info.getLocalres();
			if (localres != null && localres.size() !=0 ){				
				for(String item : localres) {	
					if(resItem.equals(item)){
						resNum++;
					}
				}						
			}				
		}
		
		
		for (Programtask insItem : insProgramtasks) {
			List<String> localres = insItem.getLocalres();
			if (localres != null && localres.size() !=0 ){				
				for(String item : localres) {	
					if(resItem.equals(item)){
						resNum++;
					}
				}						
			}		
		}	
		
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				List<String> localres = program.getLocalres();
				if (localres != null && localres.size() !=0 ){				
					for(String item : localres) {	
						if(resItem.equals(item)){
							resNum++;
						}
					}						
				}		
			}
		}	
		Log.w(TAG, "canDeleteResItem, resNum =" + resNum);
		if(resNum >1 ){
			return false;
		}
		else{
			return true;
		}
	
	}
	
	
	public synchronized void deleteLocalPorgram(Programtask prog){
		boolean isLoop = prog.isLoopProgram();
		if(!isLoop){
			if ( "localfirst".equals(prog.getOnlinemode() ) || "onlinefirst".equals(prog.getOnlinemode() ) || "local".equals(prog.getOnlinemode()) ){			
					String localResdir = CommonUtil.getMediaBasePath();
								
					if ( prog.isDownloadComplete() ) {
						List<String> localres = prog.getLocalres();
						if ( localres != null && localres.size() !=0 ){						
							for(String item : localres) {		
								Log.w(TAG, "check Local file " + item);
								if( canDeleteResItem(item) ){
									File local = new File(localResdir+File.separator+item);
									Log.w(TAG, "delete Local file " + item);
									CommonUtil.deleteDir(local);
								}															
							}						
						}				
					}
					CommonUtil.deleteAllDir(localResdir+File.separator+prog.getLocalprogramurl() );
					CommonUtil.deleteAllDir( prog.getDownloadCompleteFlag() );	
					//delete xml file
					//CommonUtil.deleteAllDir( CommonUtil.getTaskBasePath()+File.separator+prog.getProgramid());	
			}		
		}
		else{
			
		}
			
			
	}
	
	public synchronized void cleanPlayer(){
		Log.w(TAG, "cleanPlayer--------------- ");
		List<Programtask> delList = new ArrayList<Programtask>();
		for (Programtask info : programtasks) {
			if(info.canDelete() ){
				Log.w(TAG, "cleanPlayer--------------- " + info.getProgramname() );
				delList.add(info);
				deleteLocalPorgram(info);				
			}			
		}	
		if(delList.size() != 0){			
			programtasks.removeAll(delList);
		}
		
		List<Programtask> delInsList = new ArrayList<Programtask>();
		for (Programtask insItem : insProgramtasks) {
			if(insItem.canDelete() ){
				Log.w(TAG, "cleanPlayer--------------- " + insItem.getProgramname() );
				delInsList.add(insItem);
				deleteLocalPorgram(insItem);
			}			
		}	
		if(delInsList.size() != 0){
			insProgramtasks.removeAll(delList);
			return;
		}			
		
	}
	
	
	public synchronized void addLoopPlaylist(Playlist playlist){
		this.deleteLoopPlaylist(playlist.getPlaylistid() );
		if( playlist != null && loopplaylists != null){
			loopplaylists.add(playlist);	
		}
	}
	
	public synchronized void deleteLoopPlaylist(String playlistid){
		/*
		for (Playlist item : loopplaylists) {
			if(item.getPlaylistid().equals(playlistid)){
				loopplaylists.remove(item);
				return ;
			}			
		}		
		*/
		List<Playlist> delList = new ArrayList<Playlist>();
		
		for (Playlist item : loopplaylists) {
			if(item.getPlaylistid().equals(playlistid)){
				delList.add(item);
			}			
		}	
		if(delList.size() != 0){
			loopplaylists.removeAll(delList);
		}	
		
	}
	
	public synchronized Programtask getProgram(String programid){
		for (Programtask info : programtasks) {
			if(info.getProgramid().equals(programid)){
				return info;
			}			
		}	
		for (Programtask insItem : insProgramtasks) {
			if(insItem.getProgramid().equals(programid)){
				return insItem;
			}			
		}		
		// loop program
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				if(programid.equals(program.getProgramid() ) ){
					return program;
				}			
			}
		}	
		return null;		
	}
	
	public synchronized boolean isProgramExist(String programid){
		for (Programtask info : programtasks) {
			if(info.getProgramid().equals(programid)){
				return true;
			}			
		}
		for (Programtask insItem : insProgramtasks) {
			if(insItem.getProgramid().equals(programid)){
				return true;
			}			
		}
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				if(programid.equals(program.getProgramid() ) ){
					return true;
				}			
			}
		}	
		
		return false;		
	}
	
	public synchronized void clearAllProgram(){
		programtasks.clear();
		insProgramtasks.clear();		
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			protasks.clear();
		}	
		loopplaylists.clear();
		
	}

	public List<Programtask> getProgramtasks() {
		return programtasks;
	}

	public List<Programtask> getInsProgramtasks() {
		return insProgramtasks;
	}

	public int getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
		clearChangeScreen();		
	}
	
	public void clearChangeScreen(){		
		clearChangePriority();
		this.chgScrProgram = null;
		this.conflictProgram = null;
		this.chgScrDate = null;
	}
	private synchronized void clearChangePriority(){
		for (Programtask info : programtasks) {
			if( info.getPriority() == PlayerConst.priorityChangeScreen ){
				info.setPriority(PlayerConst.priorityLive);
			}
		}
		for (Programtask insItem : insProgramtasks) {
			if(insItem.getPriority() == PlayerConst.priorityChangeScreen ){
				insItem.setPriority(PlayerConst.priorityInsert);
			}			
		}
		
		// clear loop priority
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				if(program.getPriority() == PlayerConst.priorityChangeScreen ){
					program.setPriority(PlayerConst.priorityLive);
				}			
			}
		}	
	}
	public void setChangeScreen(Programtask chg){
		//clearChangePriority();
		//startPlay();
		//loop process
		setAllLoopPlayed(false);	
		MainActivity.stopLoopTask();
		Programtask current = PlayerController.getInstance().getCurrentPlay();
		if(current ==null || ( chg!= current) ){	
			clearChangePriority();
			startPlay();
			Date now = new Date();
			chg.setPriority(PlayerConst.priorityChangeScreen);
			setChgScrProgram( chg );
			setConflictProgram(current);
			setChgScrDate(now);				
		}
	}

	public boolean isDefaultPlay() {
		return defaultPlay;
	}

	public void setDefaultPlay(boolean defaultPlay) {
		this.defaultPlay = defaultPlay;
	}
	
	public synchronized void startPlay(){
		setPlayStatus(this.statusPlaying);
		setDefaultPlay(false);
	}
	
	public  void stopPlay(){
		setPlayStatus(this.statusStop );
		//clearChangeScreen();
		if(getCurrentPlay() != null){
			getCurrentPlay().setPlayed(false);
		}
		setCurrentPlay(null);		
		//loop
		setAllLoopPlayed(false);	
		MainActivity.stopLoopTask();
		//setDefaultPlay(true);
	}
	public synchronized List<Map<String, Object>> getProgramData(){
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;
		for (Programtask info : programtasks) {
			if(info.isLoopProgram()){
				continue;
			}
			item = new HashMap<String, Object>();
			item.put("programname", info.getProgramname() );
			//item.put("programname", CommonUtil.getUTF8XMLString( info.getProgramname() ) );
			
			item.put("obj", info);
			data.add(item);
		}
		for (Programtask insItem : insProgramtasks) {
			if(insItem.isLoopProgram() ){
				continue;
			}
			item = new HashMap<String, Object>();
			item.put("programname", insItem.getProgramname() );
			//item.put("programname", CommonUtil.getUTF8XMLString( insItem.getProgramname()) );
			
			item.put("obj", insItem);
			data.add(item);			
		}	
		//todo add loop play
		for (Playlist loopitem : loopplaylists) {
			Log.w(TAG, "add loop program to request play list");
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				item = new HashMap<String, Object>();
				item.put("programname", program.getProgramname() );				
				item.put("obj", program);
				data.add(item);			
			}
		}	
		
		return data;
	}

	public boolean isTurndown() {
		return turndown;
	}

	public void setTurndown(boolean turndown) {
		this.turndown = turndown;
	}
	
	public synchronized void runDownloadProgram(){
		for (Programtask info : programtasks) {
			info.downloadLocal();		
		}
		for (Programtask insItem : insProgramtasks) {
			insItem.downloadLocal();		
		}
		//add loop download
		for (Playlist loopitem : loopplaylists) {		
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				program.downloadLocal();		
			}
		}	
	}
	
	
	public synchronized Programtask getDownloadPro(){
		for (Programtask info : programtasks) {
			if(info.isNeedDownload() ){
				return info;
			}
		}
		for (Programtask insItem : insProgramtasks) {
			if(insItem.isNeedDownload() ){
				return insItem;
			}		
		}
		//add loop download
		for (Playlist loopitem : loopplaylists) {		
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				if(program.isNeedDownload() ){
					return program;
				}			
			}
		}	
		
		return null;
	}
	
	public synchronized Programtask getRunProgram(){
		Programtask target = null;
		//Log.e(TAG, "show html thead run");
		if(PlayerController.getInstance().getPlayStatus() == PlayerController.statusStop ){
			return null;			
		}
		List<Programtask> progList = PlayerController.getInstance().getProgramtasks();
		List<Programtask> insProgList = PlayerController.getInstance().getInsProgramtasks();
		Programtask schScrProgram = PlayerController.getInstance().getChgScrProgram();
		Programtask conflictProgram = PlayerController.getInstance().getConflictProgram();
		target = null;				
		Programtask cur = PlayerController.getInstance().getCurrentPlay();
		
		if (insProgList.size() !=0 ){
			Log.i(TAG, "insert program, length = "+ insProgList.size() );
			for (Programtask item : insProgList) {						
				if(item == cur || item == schScrProgram){
					continue;
				}
				if(item.canProgramRun() && item.getPlayed()==false ){							
					target = item;		
					break;
				}							
			}
		}
		if(target == null &&  progList.size() != 0 ){
			Log.i(TAG, "live program run, length = "+ progList.size() );
			for (Programtask item : progList) {
				if(item == cur || item == schScrProgram){
					continue;
				}
				if(item.canProgramRun() && item.getPlayed()==false  ){							
					target = item;
					break;
				}						
			}
		}
		if(target != null){					
			if(cur !=null && cur.canProgramRun() && target.comparePriority(cur) <= 0 ){
				target = null;												
			}					
		}
		
		if(schScrProgram != null && target != null && target.isAfterChangeScreen() ) {					
			//changescreen clear										
		}	
		else if(schScrProgram != null){
			target = schScrProgram;					
		}	
		
		if(target != null && target.getPlayed()==false){					
			//Log.i(TAG, "start new url:" + target.getUrl() );
			
			if ( target != cur && cur != null){
				cur.setPlayed(false);	
				cur.setStatus(PlayerConst.statusStop);
			}			
			PlayerController.getInstance().setCurrentPlay(target);
			target.setStatus( PlayerConst.statusPlaying);					
			if( schScrProgram != null && target != schScrProgram ){
				PlayerController.getInstance().setChgScrProgram(null);
				PlayerController.getInstance().setChgScrDate(null);
				PlayerController.getInstance().setConflictProgram(null);
			}					
			
			//Log.i(TAG, "playing ----------"+ target.getUrl());
			return target;
		}
		else{
			return null;
		}
	}

	public List<Playlist> getLoopplaylists() {
		return loopplaylists;
	}

	public void setLoopplaylists(List<Playlist> loopplaylists) {
		this.loopplaylists = loopplaylists;
	}
	
	public synchronized  Playlist getLoopPlayListEntry(String playlistid){
		for (Playlist item : loopplaylists) {
			if(item.getPlaylistid().equals(playlistid)){
				return item;
			}			
		}	
		return null;
	}
	
	public Playlist findLoopPlayList(String loopProgramid){
		Log.w(TAG, "find loop program id" + loopProgramid);
		//
		String rep = "_"+ PlayerConst.FLAG_LOOPPLAY;
		String playlistid = loopProgramid.replace(rep, "");
		return getLoopPlayListEntry(playlistid);				
	}
	
	public synchronized void setAllLoopPlayed(boolean played ){
		if(loopplaylists == null){
			return ;
		}
		for (Playlist loopitem : loopplaylists) {
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				program.setPlayed( played );
			}
		}			
	}	
	
	public synchronized boolean isInLoopList(String programid){
		for (Playlist loopitem : loopplaylists) {		
			List<Programtask>  protasks = loopitem.getProgramtasks();
			for (Programtask program : protasks) {
				if(programid.equals(program.getProgramid() )){
					return true;
				}				
			}
		}	
		return false;		
	}

	public boolean isDownloadRefresh() {
		return downloadRefresh;
	}

	public void setDownloadRefresh(boolean downloadRefresh) {
		this.downloadRefresh = downloadRefresh;
	}
	public boolean isProgramInPlaying(String programid){
		
		if(currentPlay != null && programid.equals(currentPlay.getProgramid() )){
			return true;
		}
		else{
			return false;
		}
		
	}
}
