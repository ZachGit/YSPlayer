package com.bsi.dms.player.cmd;

import android.app.Service;
import android.media.AudioManager;
import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConfig;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

//设置音量
public class VolumeControlCmd extends Command{
	private int maxVolume;
	private int currentVolume;
	public  AudioManager audiomanage; 
	XMLTaskCreate xmlCreate = null;
	
	public VolumeControlCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		String strValue = getParam("value");
		int iVolume = Integer.parseInt(strValue);
		String strTaskno = getParam("taskno");

		//获取最大音量和当前音量
		maxVolume = PlayerConfig.getMaxVolume();
		currentVolume = PlayerConfig.getCurrentVolume();
		//int flags = iVolume - Math.round(currentVolume * 100 / maxVolume);
		int flags = Math.round(iVolume * maxVolume / 100) - currentVolume;
		int direction;
		
		if (flags >= 0){
			direction = AudioManager.ADJUST_RAISE;
		}
		else{
			flags = -flags;
			direction = AudioManager.ADJUST_LOWER;
		}
		
		//设置Android终端声音
		audiomanage.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, flags);
		
		//终端向平台返回成功报文
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String getVcok = xmlCreate.createXml("ACK_OK", playerid, strTaskno, "", "");	
		
		TcpSessionThread.getInstance().sendString(getVcok);
		
		Log.e("**VolumeControl Set OK!**", getVcok);

	}

}
