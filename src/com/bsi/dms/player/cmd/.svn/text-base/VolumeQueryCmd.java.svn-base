package com.bsi.dms.player.cmd;

import android.app.Service;
import android.media.AudioManager;
import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.config.PlayerConfig;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class VolumeQueryCmd extends Command{
	private int maxVolume;
	private int currentVolume;
	public  AudioManager audiomanage; 
	
	XMLTaskCreate xmlCreate = null;
	
	public VolumeQueryCmd(Command cmd){
		super(cmd.getCommtype() , cmd.getPlayerid(), cmd.getTaskno(), cmd.getValue(), cmd.getData() );
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		//String strValue = getParam("Value");
		
		//audiomanage = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);	//获取最大音量值
		//currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
		maxVolume = PlayerConfig.getMaxVolume();
		currentVolume = PlayerConfig.getCurrentVolume();
		int iVolume = Math.round(currentVolume * 100 / maxVolume);
		
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//需要返回的音量值
		String strVolume = Integer.toString(iVolume) + ":max" + Integer.toString(maxVolume) + ":current" + Integer.toString(currentVolume);
		
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String value = Integer.toString(iVolume);
		String getvq = xmlCreate.createXml("VolumeQuery", playerid, "", value, "");	
		
		TcpSessionThread.getInstance().sendString(getvq);
		
		Log.e("DDDDDDDDDDDDDDDD", getvq);
	}
}
