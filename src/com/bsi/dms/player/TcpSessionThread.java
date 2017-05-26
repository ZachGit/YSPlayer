package com.bsi.dms.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import android.util.Log;

import com.bsi.dms.R;
import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.download.ProgramAllDownloadThread;
import com.bsi.dms.parse.CommandHandler;
import com.bsi.dms.prompt.PromptManager;
import com.bsi.dms.utils.CommonUtil;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class TcpSessionThread extends SessionThread {
	private static final String TAG = "TcpSessionThread";
	private Socket socket = null; 
	public PrintWriter out = null;
	private BufferedReader in = null;
	protected  Thread heartThread = null;
	private char[]  msg;
	private static TcpSessionThread  tcpSession = null;
	private boolean online = false; 
	private boolean inRebuild = false; 
	static{
		tcpSession = new TcpSessionThread();
	}
	public static TcpSessionThread getInstance(){
		return tcpSession;
	}	
	
	@Override
	public void initSession() {
		try {
			Log.w(TAG, "init Tcp Session");
			String host = PlayerApplication.getInstance().sysconfig.getServerip();
			int port =Integer.parseInt( PlayerApplication.getInstance().sysconfig.getServerport() );
			Log.i(TAG, host+port);
			socket = new  Socket(host, port);
			//socket.setSoTimeout(10000);
			//out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "gbk")), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gbk") );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.w(TAG,"can not connect to server");
			//e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w(TAG,"can not connect to server");
			//e.printStackTrace();
		}  
	}

	@Override
	public void startSession() {
		// TODO Auto-generated method stub
		try {
			rebuildTaskList();
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String registerContent = null;
		msg = new char[12*1024];
		while(true){
			if(socket == null || out == null || in == null || !socket.isConnected()){
				initSession();
			}
			register();
			registerContent = receiveString();			
			try {
				if (registerContent == null){
					Thread.sleep(5000);
					continue;
				}
				Command cmd = getCommand(registerContent);			
				if(cmd != null && "Register".equals( cmd.getCommtype()) ){
					Log.i(TAG, "register success");
					PlayerCmdCtr.getInstance().dispatchCommand(cmd, registerContent);  
					this.online = true;
					PromptManager.getInstance().toast(R.string.registerSuccess, PromptManager.ID_REGISTER);
					break;
				}	
				else{
					Thread.sleep(5000);
					continue;
				}
			} catch (Throwable e) {
				Log.w(TAG,"can not register to server");
				//e.printStackTrace();
			}
		}
		Log.w(TAG, "start download local resources");
		ProgramAllDownloadThread startDownload = new ProgramAllDownloadThread();
		startDownload.start();
	}
	
	private void reconnect(){
		while(true){
			if(socket == null || out == null || in == null || !socket.isConnected()){
				initSession();
			}
			register();
			String registerContent = null;
			registerContent = receiveString();			
			try {
				if (registerContent == null){
					Thread.sleep(5000);
					continue;
				}
				Command cmd = getCommand(registerContent);			
				if(cmd != null && "Register".equals( cmd.getCommtype()) ){
					Log.i(TAG, "register success");
					PlayerCmdCtr.getInstance().dispatchCommand(cmd, registerContent);  
					this.online = true;
					PromptManager.getInstance().toast(R.string.registerSuccess, PromptManager.ID_REGISTER);
					break;
				}	
				else{
					Thread.sleep(5000);
					continue;
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				Log.w(TAG,"can not register to server");
				//e.printStackTrace();
			}
		}
	}
	private void rebuildTaskList() throws Throwable{
		inRebuild = true;
		File file=new File(CommonUtil.getTaskBasePath() );
		String filesList[];
		filesList = file.list();
		Log.w(TAG, "rebuild playlist");
		if(filesList != null){
			for( int i=0; i<filesList.length; i++)
			{
				//System.out.println(test[i]);
				String xmlContent = CommonUtil.readTaskXmlString( filesList[i] );
				Log.w(TAG, "rebuild playlist"+ filesList[i]);
				Command cmd = getCommand(xmlContent);
				PlayerCmdCtr.dispatchCommand(cmd, xmlContent); 
			}	
		}
		inRebuild = false;
	}

	@Override
	public void closeSession() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendString(String msg) {
		// TODO Auto-generated method stub
		
		if(out != null && socket !=null && socket.isConnected() ){
			synchronized(out){
				out.println(msg);
			}
			Log.e(TAG, msg);
		}
	}

	private  void zeroBuf(char[] buf){
		int len = buf.length, i = 0;
		for (; i< len; i++){
			buf[i] = '\0';
		}		
	}
	
	@Override
	public String receiveString() {
		String content = null;
		int len = 0;
		if (in != null && socket != null && socket.isConnected()) {    			
			synchronized(in){
				try {
		 			zeroBuf(msg);
		 			int off = 0;
		 			int readlen = 0;
		 			
		 			len = in.read(msg, 0, 12*1024);
		 			if(len == -1 ){
						initSession();
						register();
						return null;
					}							 			
		 			else{
		 				while(!isCommandEnd(msg, len ) ){
		 					readlen = in.read(msg, len, 12*1024 - len);
		 					if(readlen != -1){
		 						len += readlen;
		 					}
		 					else{
		 						break;
		 					}
		 				}
		 			}		 			
					content = new String(msg, 0, len);
	            	Log.e(TAG, "receive content:"+content);
		 		} catch (IOException e) {
					e.printStackTrace();
				} 
			}
        }  
		else{			
			initSession();		
		}
		return content;
	}
	
	private void closeSocketAndStreams(){
		Log.w(TAG, "close TCP Session");
		CommonUtil.closeStream(in);
		in = null;
		CommonUtil.closeStream(out);
		out = null;
		CommonUtil.closeSocket(socket);
		socket = null;
	}
	
	public boolean isCommandEnd(char[] msg, int len){
		String msgStr = new String(msg, 0, len);
		return msgStr.contains("</Command>");
	}
	
	@Override
	public void run()  {
		// TODO Auto-generated method stub
		//super.run();
		startSession();
	    Log.i(TAG, "tcp SessionThread run");
	    heartbeat();

	    while (true) {  
	    	Log.i(TAG, "receive server run");
            if (socket != null && socket.isConnected()) {             	
                	Log.i(TAG, "receive run");                   	
                	try {
                		String content = receiveString();
                		if(content != null){
                		 } 
                		else{
                			Thread.sleep( 1000 );
                			continue;
                		}
                		            		
                		//content = "<?xml version='1.0' encoding='utf-8'?><Command  CommType='ChgScreen' PlayerID='132' TaskNO=''><Value>50:46:5D:67:5A:C8|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device</Value><Data></Data></Command>";
                		/*
                		String content ="<?xml version='1.0' encoding='utf-8'?>"+
                				" <Command CommType='LivePlay' PlayerID='654321' TaskNO='RP20130220'>"+
                				" <Value> </Value>"+
                				" <Data>"+ 
                				" <PlayList PlayListID='Playlist01' PlayListName='ok' SiteID='siteid'>"+     
                				" <Programtask ProgramID='1234' ProgramName='programNameOK'>"+
                				" <StartDate>2013-04-17</StartDate>"+
                				" <EndDate>2014-01-01</EndDate>"+
                				"  <StartTime>01:10:10</StartTime>"+
                				" <EndTime>10:10:10</EndTime>"+
                				" <Week>0123</Week>"+
                				" <CycleIndex></CycleIndex>"+
                				" <Time>5000</Time>"+
                				" <url>http://www.google.com.hk</url>"+
                				" </Programtask>"+                		                  
                				" <Programtask ProgramID='1235' ProgramName='ProgramNameOk2'>"+
                				" <StartDate>2013-3-1</StartDate>"+
                				" <EndDate>2013-5-1</EndDate>"+
                				" <StartTime>10:10:10</StartTime>"+
                				" <EndTime>10:10:10</EndTime>"+
                				" <Week></Week>"+
                				" <CycleIndex></CycleIndex>"+
                				" <Time>5000</Time>"+
                				" <url>http://www.51bsi.com</url>"+
                				"  </Programtask>"+
                				" </PlayList>"+ 
                				" </Data>"+          
                				" </Command>";
                		*/
                		Command cmd = getCommand(content);
                		PlayerCmdCtr.dispatchCommand(cmd, content);  
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
            }  
        }  
		
	}
	
	public  Command getCommand(String xmlCommand) throws Throwable{
		if(xmlCommand == null || xmlCommand.length() == 0 ){
			return null;
		}
		StringReader read = new StringReader(xmlCommand);
		InputSource source = new InputSource(read);
		CommandHandler pHandler = new CommandHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    SAXParser saxParser = spf.newSAXParser();
	    saxParser.parse(source, pHandler);
	    List<Command> cmds = pHandler.getCommands();
    	if(cmds == null){
    		return null;
    	}
    	return cmds.get(0);
	}
				
	private boolean register()	{
		//String message = "<?xml version='1.0' encoding='utf-8'?><Command  CommType='Register' PlayerID='654331' TaskNO=''><Value>50:46:5D:67:5A:C8|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device</Value><Data></Data></Command>";
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.online = false;
		String message = xmlCreate.createRegisterXml();
		Log.i(TAG, message);
		sendString(message);
		return true;
	}
	
	private void heartbeat() {
		if(heartThread == null){
			heartThread = new Thread(new HeartRunnable());		
			heartThread.start();
		}
	}
	
	public class HeartRunnable implements Runnable{
		@Override
		public void run() {
			Integer secHeart = PlayerApplication.getInstance().sysconfig.getHeartbeattime();;

			while(true){
				XMLTaskCreate xmlCreate = null;
				try {
					xmlCreate = new XMLTaskCreate();				
					String message = xmlCreate.createHeartbeatXml();
					sendString(message);			
					Thread.sleep(secHeart*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}			
		}
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isInRebuild() {
		return inRebuild;
	}

	public void setInRebuild(boolean inRebuild) {
		this.inRebuild = inRebuild;
	}
	
	

}
