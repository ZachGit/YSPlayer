package com.bsi.dms.player.cmd;

import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

//设置屏幕分辨率
public class SetScreenCmd extends Command{
	XMLTaskCreate xmlCreate = null;
	public SetScreenCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		String value = getParam("value");
		String[] values = value.split("\\*");
		String strTaskno = getParam("taskno");
		
		int width = Integer.parseInt(values[0]);
		int height = Integer.parseInt(values[1]);
		int rate = Integer.parseInt(values[2]);
		
		//需要添加分辨率设置接口
		//Android终端不支持设置屏幕分辨率，需确认此处如何实现
		
		//终端向平台返回成功报文
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String getSetSCok = xmlCreate.createXml("ACK_OK", playerid, strTaskno, "", "");	
				
		TcpSessionThread.getInstance().sendString(getSetSCok);
				
		Log.e("**SetScreen Set OK!**", getSetSCok);
	}
}
