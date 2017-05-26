package com.bsi.dms.player.cmd;

import com.bsi.dms.bean.Command;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

//直播
public class LivePlayCmd extends Command{
	XMLTaskCreate xmlCreate = null;	
	public LivePlayCmd(Command cmd){		
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
		
	}

}
