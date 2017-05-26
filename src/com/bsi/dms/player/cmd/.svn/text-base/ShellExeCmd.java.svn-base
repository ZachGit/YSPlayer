package com.bsi.dms.player.cmd;

import java.io.IOException;

import android.text.TextUtils;
import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class ShellExeCmd extends Command{
	XMLTaskCreate xmlCreate = null;
	public ShellExeCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		String data = getParam("data");
		
		String playerid = getParam("playerid");
		String taskno = getParam("taskno");
		
		if(TextUtils.isEmpty(data) ){
			try {
				Runtime.getRuntime().exec(data);
				String csOk = xmlCreate.createXml("ACK_OK", playerid, taskno, "", "");	
				TcpSessionThread.getInstance().sendString(csOk);
				
			} catch (IOException e) {
				Log.w("ShellExeCmd", "ShellExeCmd fail ");
				String csFail = xmlCreate.createXml("ACK_FAILED", playerid, taskno, "", "");	
				TcpSessionThread.getInstance().sendString(csFail);
				
			}
			
		}
		else{
			String csFail = xmlCreate.createXml("ACK_FAILED", playerid, taskno, "没有该节目！", "");	
			TcpSessionThread.getInstance().sendString(csFail);
		}
		
		
	}
	
	
	

}
