package com.bsi.dms.player.cmd;

import android.content.Intent;

import com.bsi.dms.bean.Command;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

//关机
public class TurnDownCmd extends Command{
	XMLTaskCreate xmlCreate = null;
	public TurnDownCmd(Command cmd){
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
		
		PlayerController.getInstance().setTurndown(true);
	}

}
