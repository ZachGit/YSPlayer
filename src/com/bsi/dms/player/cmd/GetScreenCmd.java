package com.bsi.dms.player.cmd;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConfig;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class GetScreenCmd extends Command{
	XMLTaskCreate xmlCreate = null;
	public GetScreenCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	private static final String GCTAG = "BBBBBBBBB Send Screen msg!";
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
//		Log.e("ccccccccccccccccccc", "hahahaha");
//		String value = getParam("Value");
//		String[] values = value.split("\\*");
		
		//Display display =((WindowManager) BaseActivity.getCurrent().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = PlayerConfig.getWidth();
		int height = PlayerConfig.getHeight();
		int rate = PlayerConfig.getRate();
		
		String strSC = Integer.toString(width) + "*" + Integer.toString(height) + "*" + Integer.toString(rate);
		
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String value = strSC;
		String getsc = xmlCreate.createXml("GetScreen", playerid, "", value, "");	
		
		TcpSessionThread.getInstance().sendString(getsc);
		
		Log.e(GCTAG, getsc);
	}
}
