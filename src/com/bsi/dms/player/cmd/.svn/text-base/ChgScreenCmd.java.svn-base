package com.bsi.dms.player.cmd;

import java.util.Date;

import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.bean.Programtask;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

//切屏
public class ChgScreenCmd extends Command{
	private static final String TAG = "ChgScreenCmd";
	XMLTaskCreate xmlCreate = null;
	public ChgScreenCmd( Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	
	public ChgScreenCmd(String commtype, String playerid, String taskno, String value, String data){
		super(commtype, playerid, taskno, value, data);
	}

	@Override
	public void run() {
		super.run();
		//add your run process		
		String playerid = getParam("playerid");
		String taskno = getParam("taskno");
		String programid = getParam("value");
		Log.e(TAG, "change screen programid =" + programid);
		
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//返回切屏执行结果
		Programtask sc = PlayerController.getInstance().getProgram(programid);
		if ( sc == null){
			String csFail = xmlCreate.createXml("ACK_FAILED", playerid, taskno, "没有该节目！", "");	
			TcpSessionThread.getInstance().sendString(csFail);
		}
		else{
			String csOk = xmlCreate.createXml("ACK_OK", playerid, taskno, "", "");	
			TcpSessionThread.getInstance().sendString(csOk);
			
			PlayerController.getInstance().setChangeScreen(sc);
			/*
			Programtask current = PlayerController.getInstance().getCurrentPlay();
			if(current ==null || ( programid!=null && ( !programid.equals(current.getProgramid() ) )) ){	
				Date now = new Date();
				sc.setPriority(PlayerConst.priorityChangeScreen);
				PlayerController.getInstance().setChgScrProgram( sc );
				PlayerController.getInstance().setConflictProgram(current);
				PlayerController.getInstance().setChgScrDate(now);				
			}
			*/
			Log.e("**ChgScreen is OK**", csOk);
		}		
	}	
}
