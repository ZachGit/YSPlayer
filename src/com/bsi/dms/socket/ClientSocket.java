package com.bsi.dms.socket;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter; 

import android.app.Activity;  
import android.app.AlertDialog;  
import android.content.DialogInterface;  
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Message; 

public class ClientSocket {
	
	//private TextView tv_msg = null;  
	//private EditText ed_msg = null;  
	//private Button btn_send = null;  
	//private Button btn_login = null;  
	private static final String HOST = "192.168.7.195";  
	private static final int PORT = 9800;  
	private static final int timeout = 1000;
	private Socket socket = null;  
	private BufferedReader in = null;  
	private PrintWriter out = null;  
	private String content = "";
	
	public String socketHandler(){
			try {  
	            socket = new Socket(HOST, PORT);  
	            socket.setSoTimeout(timeout);
	            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
	            //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);  
	            out = new PrintWriter(socket.getOutputStream(), true);
	            // 创建读取数据的Reader,里面指定了需要的编码类型。
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				content = in.readLine();
				//responseString = Base64.getFromBASE64(responseString);
				out.close();
				in.close();
				socket.close();
				
				//return content;
				
	        } catch (IOException ex) {  
	            ex.printStackTrace();  
	            //ShowDialog("login exception" + ex.getMessage());  
	        }
			
			return content;
	}
	
	/*
	public void ShowDialog(String msg) {  
        new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)  
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {      	
  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        // TODO Auto-generated method stub  
  
                    }  
                }).show();  
    } 
    */ 
	

}
