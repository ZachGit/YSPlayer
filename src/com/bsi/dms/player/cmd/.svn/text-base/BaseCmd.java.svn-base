package com.bsi.dms.player.cmd;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public abstract class BaseCmd extends Thread{

	public String name;
	private  Map<String, String> paramMap =  new HashMap<String, String>();
			
	public BaseCmd( ){
		
	}
	
	public BaseCmd(String initname){
		name = initname;		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		Log.i("BaseCmd", "BaseCmd run");
	}
	
	abstract public void formatParam();
	
	public String getParam(String key){
		return paramMap.get(key);
	}
	
	public void addParam(String key, String value){
		paramMap.put(key,value);		
	}
}
