package com.bsi.dms.player.cmd;

import android.content.Context;
import android.os.PowerManager;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerConfig;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;
//重启
public class RebootCmd extends Command{
	XMLTaskCreate xmlCreate = null;	
	public RebootCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		
		String playerid = getParam("playerid");
		String taskno = getParam("taskno");
		
		
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String csOk = xmlCreate.createXml("ACK_OK", playerid, taskno, "", "");	
		TcpSessionThread.getInstance().sendString(csOk);
		
		//设置系统重启
		//PowerManager pManager=(PowerManager)getSystemService(Context.POWER_SERVICE);  
		PlayerConfig.pManager.reboot("Reboot Command!");
		/*
		try {
			Log.w("RebootCmd", "reboot -------------");
			Runtime.getRuntime().exec("su -c \"/system/bin/reboot\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

		
	}

}
