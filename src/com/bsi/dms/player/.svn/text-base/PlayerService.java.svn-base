package com.bsi.dms.player;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bsi.dms.bean.Programtask;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.utils.CommonUtil;

public class PlayerService extends Service implements Runnable {
	private static final String TAG = "PlayerService";

	public static final int WAKE_INTERVAL_MS = 1000; // milliseconds
	
	protected static Thread playerThread = null;
	protected boolean shouldExit = false;
	
	//Socket clientSocket;
	
	public PlayerService() {
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, " start player service");
		//String host = ((PlayerApplication) this.getApplication()).sysconfig.getServerip();
		//int port =Integer.parseInt( ((PlayerApplication) this.getApplication()).sysconfig.getServerport() );
		//Log.i(TAG, host+port);
		super.onStart(intent, startId);
		 shouldExit = false;
		 /*
	        int attempts = 10;
	        // The previous server thread may still be cleaning up, wait for it
	        // to finish.
	        while (playerThread != null) {
	            Log.w(TAG, "Won't start, server thread exists");
	            if (attempts > 0) {
	                attempts--;
	                CommonUtil.sleepIgnoreInterupt(1000);
	            } else {
	                Log.w(TAG, "Server thread already exists");
	                return;
	            }
	        }
	        */
	        Log.d(TAG, "Creating server thread");
	        if (!isRunning() ){
	        	playerThread = new Thread(this);
	        	playerThread.start();
	        }
	}

    public static boolean isRunning() {
        // return true if and only if a server Thread is running
        if (playerThread == null) {
            Log.d(TAG, "Server is not running (null serverThread)");
            return false;
        }
        if (!playerThread.isAlive()) {
            Log.d(TAG, "serverThread non-null but !isAlive()");
        } else {
            Log.d(TAG, "Server is alive");
        }
        return true;
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
	
		//TcpSessionThread  tcpSession = new TcpSessionThread(this);
		
		TcpSessionThread  tcpSession = TcpSessionThread.getInstance();
		tcpSession.start();
        		
		while(true){
			try {
	            // todo: think about using ServerSocket, and just closing
	            // the main socket to send an exit signal
				//Log.i(TAG, " download thread run");
				//PlayerController.getInstance().runDownloadProgram();								
	            Thread.sleep(3600000);
	            //CommonUtil.clearLogData();  //only store an hour log
	            Log.w(TAG, "clean Local disk------");				
				PlayerController.getInstance().cleanPlayer();
				
	        } catch (InterruptedException e) {
	            Log.d(TAG, "Thread interrupted");
	        }
		}
		
	}
	
	private boolean loadSettings() {
		return true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy() Stopping server");
        shouldExit = true;
        if (playerThread == null) {
            Log.w(TAG, "Stopping with null serverThread");
            return;
        } else {
            playerThread.interrupt();
            try {
                playerThread.join(10000); // wait 10 sec for server thread to
                                          // finish
            } catch (InterruptedException e) {
            }
            if (playerThread.isAlive()) {
                Log.w(TAG, "Server thread failed to exit");
                // it may still exit eventually if we just leave the
                // shouldExit flag set
            } else {
                Log.d(TAG, "serverThread join()ed ok");
                playerThread = null;
            }
        }
	}
	
}
