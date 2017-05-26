package com.bsi.dms.player;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public abstract class SessionThread extends Thread {
	
	private static final String TAG = "SessionThread";
	protected Socket dataSocket = null;
	public OutputStream dataOutputStream = null;
	//public PlayerService  playerService = null;

	public SessionThread(){
		//playerService = playerService;
	}
	abstract public void initSession();
	abstract public void startSession();
	abstract public void closeSession();
	abstract public void sendString(String msg);
	abstract public String receiveString();
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
           // BufferedReader in = new BufferedReader(dataSocket, 8192); // use 8k buffer               
		 //String cmd = new String();
         //PlayerCmd.dispatchCommand(this, cmd);               
       
	}
	
    public boolean startUsingDataSocket() {
        try {
            //dataSocket = dataSocketFactory.onTransfer();
            if (dataSocket == null) {
                Log.i(TAG, "dataSocketFactory.onTransfer() returned null");
                return false;
            }
            dataOutputStream = dataSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            Log.i(TAG, "IOException getting OutputStream for data socket");
            dataSocket = null;
            return false;
        }
    }
	
}
