package com.bsi.dms.download;

import com.bsi.dms.bean.Programtask;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.utils.CommonUtil;

public class ProgramAllDownloadThread extends Thread{
	private boolean currentOnline = false;
	private boolean registered = false;
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		//PlayerController.getInstance().runDownloadProgram();
		
		while(true ){		
			
			currentOnline = PlayerApplication.isNetworkAvailable();			
			registered = TcpSessionThread.getInstance().isOnline();
			if(!currentOnline || !registered ){
				try {
					Thread.sleep(20000);
					continue;
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
					
			Programtask downPro = PlayerController.getInstance().getDownloadPro();
			if(downPro != null ){
				downPro.downloadLocal();
				//less than 40M
				if (CommonUtil.getFreeSpaceSize() <= 40000000) {
					PlayerController.getInstance().cleanPlayer();					
				}
			}
			else{
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
		}		
	}
}
