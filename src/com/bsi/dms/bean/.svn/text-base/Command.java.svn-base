package com.bsi.dms.bean;

import com.bsi.dms.player.cmd.BaseCmd;

public class Command extends BaseCmd{
	private String commtype;
	private String playerid;
	private String taskno;
	private String value;
	private String data;

	public Command(){
		
	}
	public Command(String commtype){
		super(commtype);
	}
	
	public Command(String commtype, String playerid, String taskno, String value, String data) {
		this.commtype = commtype;
		this.playerid = playerid;
		this.taskno = taskno;
		this.value = value;
		this.data = data;
	}
	
	public Command(String value, String data) {
		this.value = value;
		this.data = data;
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
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	@Override
	public void formatParam(){
		addParam("commtype", playerid);
		addParam("playerid", playerid);
		addParam("taskno", taskno);
		addParam("value", value);
		addParam("data", data);		
	}
	
	@Override
	public String toString() {
		return "commtype="+ commtype+ ",playerid="+ playerid+ ",taskno="+ taskno + ",value="+ value + ",data="+ data;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		formatParam();
	}
	
	

}
