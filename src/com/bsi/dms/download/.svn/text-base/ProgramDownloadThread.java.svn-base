package com.bsi.dms.download;

import com.bsi.dms.bean.Programtask;

public class ProgramDownloadThread extends Thread {
	Programtask  downProgram;
	public ProgramDownloadThread(Programtask  p){
		downProgram = p;			
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		if(downProgram == null){
			return;
		}
		downProgram.downloadLocal();
		
	}
}
