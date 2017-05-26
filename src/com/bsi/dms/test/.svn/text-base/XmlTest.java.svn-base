package com.bsi.dms.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.ProgramVideo;
import com.bsi.dms.bean.Syscfg;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.parse.CommandHandler;
import com.bsi.dms.parse.PlaylistHandler;
import com.bsi.dms.parse.SyscfgHandler;
import com.bsi.dms.parse.VideoHandler;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class XmlTest {
	private static final String TAG = "XmlTest"; 
	public  void main_XmlTest(){
		try{
			testSAXGetCommand();
			testSAXGetSyscfg();
			testSAXGetPlaylist();
			testCreateRegisterXml();
			testCreateHeartbeatXml();
			testXmlString();
			//testDownloadXml();
			testSAXGetVideo();
			testloopPlayList();
		}
	    catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	
	public  void testSAXGetCommand() throws Throwable{
		InputStream inputStream = this.getClass().getClassLoader().
				getResourceAsStream("command.xml");
		CommandHandler pHandler = new CommandHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
    	SAXParser saxParser = spf.newSAXParser();
    	saxParser.parse(inputStream, pHandler);
    	List<Command> cmds = pHandler.getCommands();
    	inputStream.close();
    	for(Command c:cmds){
    		Log.i(TAG, c.toString());
    	}
	}
	

	public  void testSAXGetSyscfg() throws Throwable{
		InputStream inputStream = this.getClass().getClassLoader().
				getResourceAsStream("syscfg.xml");
		SyscfgHandler pHandler = new SyscfgHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
    	SAXParser saxParser = spf.newSAXParser();
    	saxParser.parse(inputStream, pHandler);
    	List<Syscfg> cfgs = pHandler.getSyscfgs();
    	inputStream.close();
    	for(Syscfg  c:cfgs){
    		Log.i(TAG, c.toString());
    	}
	}	
	
	public  void testSAXGetPlaylist() throws Throwable{
		InputStream inputStream = this.getClass().getClassLoader().
				getResourceAsStream("playlist.xml");
		PlaylistHandler pHandler = new PlaylistHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
    	SAXParser saxParser = spf.newSAXParser();
    	saxParser.parse(inputStream, pHandler);
    	
    	//Playlist play = pHandler.getPlaylist();
    	Playlist play = pHandler.getPlaylists().get(0);
    	inputStream.close();
    	Log.i(TAG, play.toString());
    	}

	public void testCreateRegisterXml(){
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ret = xmlCreate.createRegisterXml();
		Log.i(TAG, ret);
	}
	
	public void testCreateHeartbeatXml(){
		XMLTaskCreate xmlCreate = null;
		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ret = xmlCreate.createHeartbeatXml();
		Log.i(TAG, ret);
	}
	
	public void testXmlString() throws ParserConfigurationException, SAXException, IOException{		
		String xmlCommand = "<?xml version='1.0' encoding='utf-8'?><Command  CommType='Register' PlayerID='132' TaskNO=''><Value>50:46:5D:67:5A:C8|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device</Value><Data></Data></Command> ";
		StringReader read = new StringReader(xmlCommand);
		InputSource source = new InputSource(read);
		CommandHandler pHandler = new CommandHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    SAXParser saxParser = spf.newSAXParser();
	    saxParser.parse(source, pHandler);
	    List<Command> cmds = pHandler.getCommands();
	    if(cmds != null){
	     Log.i(TAG, cmds.get(0).toString() );		
	    }
	}
	
	public void testDownloadXml(){
		Log.e(TAG, "DownloadEngine use######################" );
		File save = new File("/mnt/nand/Files/");
		//Context context = getApplicationContext();
		DownloadEngine.getInstance().download("http://192.168.9.24/dms/MediaStore/PlayList/5/5.html",  save);		
	}
	
	public  void testSAXGetVideo() throws Throwable{
		InputStream inputStream = this.getClass().getClassLoader().
				getResourceAsStream("programvideo.xml");
		VideoHandler pHandler = new VideoHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
    	SAXParser saxParser = spf.newSAXParser();
    	saxParser.parse(inputStream, pHandler);    	
    	ProgramVideo video = pHandler.getTarget();
    	if(video != null){
    		Log.e(TAG, "video:" + video.getLeft() + video.getTop() + video.getWidth() + video.getHeight() );
    	}
    
	}
	
	public  void testloopPlayList() throws Throwable{
		InputStream inputStream = this.getClass().getClassLoader().
				getResourceAsStream("loopPlayList.xml");
		PlaylistHandler pHandler = new PlaylistHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
    	SAXParser saxParser = spf.newSAXParser();
    	saxParser.parse(inputStream, pHandler);
    	
    	//Playlist play = pHandler.getPlaylist();
    	Playlist play = pHandler.getPlaylists().get(0);
    	inputStream.close();
    	Log.i(TAG, play.toString());
    
	}
	
}
