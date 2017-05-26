package com.bsi.dms.player.cmd;

import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.utils.CommonUtil;

public class HeartbeatCmd extends Command{ 
	static boolean synTime = false;
	
	public HeartbeatCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();		
		String value = getParam("value");
		if(synTime == false && value != null  ){
			Log.w("HeartbeatCmd", "set system time");
			CommonUtil.setDate(value);
			synTime = true;
		}
		CommonUtil.WriteLog("终端会话","收到心跳包");
	}
	

}
