package com.bsi.dms.bean;

import java.util.List;

import com.bsi.dms.config.PlayerConst;

public class Playlist {
	private String commtype;
	private String playerid;
	private String taskno;
	private String value;
	private String data;
	
	private String playlistid;
	private String playlistname;
	private String siteid;
	private List<Programtask> programtasks;	
	private String plantype;
	private String loopstartdate;
	private String loopenddate;
	private String loopstarttime;
	private String loopendtime;
	private String loopweek;

	
	public Playlist(){
		
	}
	
	public Playlist(String playlistid, String playlistname, String siteid, String programid, String programname, 
			String startdate, String enddate, String starttime, String endtime, String week, String cycleindex, String time, String url) {
		this.playlistid = playlistid;
		this.playlistname = playlistname;
		this.siteid = siteid;

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
	
	public List<Programtask> getProgramtasks() {
		return programtasks;
	}

	public void setProgramtasks(List<Programtask> programtasks) {
		this.programtasks = programtasks;
	}

	@Override
	public String toString() {
		
//		return "startdate="+ startdate+ ",enddate="+ enddate+ ",starttime="+ starttime+ ",endtime="+ endtime+ ",week="+ week+ ",cycleindex="+ cycleindex+ ",time="+ time+ ",url="+ url;
		return "playlistid="+ playlistid + ",playlistname="+ playlistname+ ",siteid="+ siteid;
	 }

	public String getLoopstartdate() {
		return loopstartdate;
	}

	public void setLoopstartdate(String loopstartdate) {
		this.loopstartdate = loopstartdate;
	}

	public String getLoopenddate() {
		return loopenddate;
	}

	public void setLoopenddate(String loopenddate) {
		this.loopenddate = loopenddate;
	}

	public String getLoopstarttime() {
		return loopstarttime;
	}

	public void setLoopstarttime(String loopstarttime) {
		this.loopstarttime = loopstarttime;
	}

	public String getLoopendtime() {
		return loopendtime;
	}

	public void setLoopendtime(String loopendtime) {
		this.loopendtime = loopendtime;
	}

	public String getLoopweek() {
		return loopweek;
	}

	public void setLoopweek(String loopweek) {
		this.loopweek = loopweek;
	}

	public String getPlantype() {
		return plantype;
	}

	public void setPlantype(String plantype) {
		this.plantype = plantype;
	}
	
	public Programtask createLoopProgram(){
		Programtask  loop = null;
		if("loop".equals(this.getPlantype() )){
			loop = new Programtask();
			loop.setStartdate(this.loopstartdate);
			loop.setStarttime(this.loopstarttime);
			loop.setEnddate(this.loopenddate);
			loop.setEndtime(this.loopendtime);
			loop.setWeek(this.loopweek);
			
			loop.setProgramid(this.playlistid + "_"+ PlayerConst.FLAG_LOOPPLAY);
			
			loop.addPlaylistParam(this.getPlaylistid(), this.getPlaylistname(), this.getSiteid());
			return loop;				
		}
		else{
			return null;
		}
		
	}
}
