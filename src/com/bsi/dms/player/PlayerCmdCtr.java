package com.bsi.dms.player;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.bsi.dms.R;
import com.bsi.dms.bean.Command;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.Programtask;
import com.bsi.dms.download.DownloadEngine;
import com.bsi.dms.download.ProgramDownloadThread;
import com.bsi.dms.parse.PlaylistHandler;
import com.bsi.dms.player.cmd.ChgScreenCmd;
import com.bsi.dms.player.cmd.GetScreenCmd;
import com.bsi.dms.player.cmd.HeartbeatCmd;
import com.bsi.dms.player.cmd.InterPlayCmd;
import com.bsi.dms.player.cmd.LivePlayCmd;
import com.bsi.dms.player.cmd.OnPlayCmd;
import com.bsi.dms.player.cmd.PublishCmd;
import com.bsi.dms.player.cmd.RebootCmd;
import com.bsi.dms.player.cmd.RegisterCmd;
import com.bsi.dms.player.cmd.SetScreenCmd;
import com.bsi.dms.player.cmd.ShellExeCmd;
import com.bsi.dms.player.cmd.StopPlayCmd;
import com.bsi.dms.player.cmd.SystemInfoCmd;
import com.bsi.dms.player.cmd.TurnDownCmd;
import com.bsi.dms.player.cmd.UpdateCmd;
import com.bsi.dms.player.cmd.VolumeControlCmd;
import com.bsi.dms.player.cmd.VolumeQueryCmd;
import com.bsi.dms.prompt.PromptManager;
import com.bsi.dms.utils.CommonUtil;

public class PlayerCmdCtr {
	private static final String TAG = "PlayerCmdCtr";
	private static PlayerCmdCtr playerCmdController; 
	static {
		playerCmdController = new PlayerCmdCtr();
	}
	
	public static PlayerCmdCtr getInstance(){
		return playerCmdController;
	}

	public static void dispatchCommand(Command cmd, String xmlContent){
		boolean xmlWrite = false;
		if(cmd == null){
			return ;
		}
		Programtask lastInter = null;
		if("ChgScreen".equals(cmd.getCommtype())){
			ChgScreenCmd newCmd = new ChgScreenCmd(cmd);
			newCmd.start();		
		}
		else if("LivePlay".equals(cmd.getCommtype()) || "InterPlay".equals(cmd.getCommtype()) || "Publish".equals(cmd.getCommtype()) ) {
			if( "Publish".equals(cmd.getCommtype()) ){
				PlayerController.getInstance().clearAllProgram();
				//CommonUtil.deleteDir(new File(CommonUtil.getTaskBasePath() ) );
				CommonUtil.clearAllLocalData(); 
			}
			PlayerController.getInstance().startPlay();
			PlayerController.getInstance().clearChangeScreen();
			try {
				List<Playlist> p = getAllPlaylists(xmlContent);								
				if (p==null || p.isEmpty() ){
					return;
				}	
				/*
				if( !TcpSessionThread.getInstance().isInRebuild() ){
					Log.w(TAG, "write task to xml file");
					CommonUtil.writeTaskXml(xmlContent, p.get(0).getPlaylistid() +".xml");
				}
				*/
				for(Playlist listitem:p){
					// add loop playlist	
					if( "loop".equals(listitem.getPlantype()) ){
						if(xmlWrite == false){
							if( !TcpSessionThread.getInstance().isInRebuild() ){
								Log.w(TAG, "write task to xml file");
								CommonUtil.writeTaskXml(xmlContent, p.get(0).getPlaylistid() +".xml");
							}
							xmlWrite = true;
						}
						Log.w(TAG, "add loop program -----------");
						PlayerController.getInstance().addLoopPlaylist( listitem );
						//create new loop program
						//Programtask loopTask = new Programtask();
						Programtask loopTask = listitem.createLoopProgram();
						loopTask.addCommandParam(cmd.getCommtype(), cmd.getPlayerid(), cmd.getTaskno());
						PlayerController.getInstance().addProgram(loopTask);
						
						List<Programtask> loop = listitem.getProgramtasks();
						if (loop.isEmpty() ){
							continue;
						}
						
						for(Programtask item:loop){					
							if ( "localfirst".equals(item.getOnlinemode()) || "onlinefirst".equals(item.getOnlinemode()) ||"local".equals(item.getOnlinemode()) ){
								Programtask old = PlayerController.getInstance().getProgram(item.getProgramid() );
								if(old !=null && old.isInDownloading() ){
									Log.w(TAG, "not update the downloading program-------" );
									PromptManager.getInstance().toast(R.string.indownloading, PromptManager.ID_COMMAND);
									continue;    
								}
								else if(old !=null && old.isDownloadComplete() ){
									CommonUtil.deleteDir(new File(old.getDownloadCompleteFlag() ));
								}
							}

							
							item.addCommandParam(cmd.getCommtype(), cmd.getPlayerid(), cmd.getTaskno()); 
							item.addPlaylistParam(listitem.getPlaylistid(), listitem.getPlaylistname(), listitem.getSiteid());
							item.addPlaylistDate(listitem.getLoopstartdate(),listitem.getLoopenddate(), listitem.getLoopstarttime(), listitem.getLoopendtime(), listitem.getLoopweek() );
							
							//download xml
							//downloadProgramXML(item);
							//download local
							
							/*
							if ( "localfirst".equals(item.getOnlinemode()) || "onlinefirst".equals(item.getOnlinemode()) || "local".equals(item.getOnlinemode()) ){
								if( !TcpSessionThread.getInstance().isInRebuild() ){
									if(!item.isDownloadComplete() ){
										ProgramDownloadThread proThread = new ProgramDownloadThread(item);
										proThread.start();
									}
								}
							}
							*/
						}	
						
						//insert and response
						if(/*"InterPlay".equals(cmd.getCommtype()) && */loopTask.canProgramRun() ){
							lastInter = loopTask;							
						}
						if(lastInter != null){
							Log.e(TAG, "last inter play -------" );
							PlayerController.getInstance().setChangeScreen(lastInter);
							lastInter = null;
						}
						if( !TcpSessionThread.getInstance().isInRebuild() ){
							if( "LivePlay".equals(cmd.getCommtype()) ){
								LivePlayCmd liveCmd = new LivePlayCmd(cmd);
								liveCmd.start();
							}
							else if( "InterPlay".equals(cmd.getCommtype()) ){
								InterPlayCmd  interCmd = new InterPlayCmd(cmd);
								interCmd.start();
							}
						}
						continue;						
					}
					
					// esle : not loop
					List<Programtask> l = listitem.getProgramtasks();
					if (l.isEmpty() ){
						continue;
					}
					for(Programtask item:l){						
						if ( "localfirst".equals(item.getOnlinemode()) || "onlinefirst".equals(item.getOnlinemode()) || "local".equals(item.getOnlinemode())){
							Programtask old = PlayerController.getInstance().getProgram(item.getProgramid() );
							if(old !=null && old.isInDownloading() ){
								Log.w(TAG, "not update the downloading program-------" );
								PromptManager.getInstance().toast(R.string.indownloading, PromptManager.ID_COMMAND);
								continue;    //not update downloading program
							}
							else if(old !=null && old.isDownloadComplete() ){
								Log.w(TAG,"update local program");
								CommonUtil.deleteDir(new File(old.getDownloadCompleteFlag() ));
							}
						}
						if(xmlWrite == false){
							if( !TcpSessionThread.getInstance().isInRebuild() ){
								Log.w(TAG, "write task to xml file");
								CommonUtil.writeTaskXml(xmlContent, p.get(0).getPlaylistid() + "_" + item.getProgramid() + ".xml");
							}
							xmlWrite = true;
						}
						item.addCommandParam(cmd.getCommtype(), cmd.getPlayerid(), cmd.getTaskno()); 
						item.addPlaylistParam(listitem.getPlaylistid(), listitem.getPlaylistname(), listitem.getSiteid());
						if(PlayerController.getInstance().isProgramInPlaying(item.getProgramid() )){
							PlayerController.getInstance().setCurrentPlay(null);
						}
						PlayerController.getInstance().addProgram(item);
						if(/*"InterPlay".equals(cmd.getCommtype()) &&*/ item.canProgramRun() ){
							lastInter = item;							
						}
						//download xml
						//downloadProgramXML(item);
						//download local
						/*
						if ( "localfirst".equals(item.getOnlinemode()) || "onlinefirst".equals(item.getOnlinemode()) || "local".equals(item.getOnlinemode()) ){
							if( !TcpSessionThread.getInstance().isInRebuild() ){
								if(!item.isDownloadComplete() ){
									Log.e(TAG, "download local resource ----------");
									ProgramDownloadThread proThread = new ProgramDownloadThread(item);
									proThread.start();
								}
							}
						}
						*/
					}
				}
				// InterPlay changeto chgScreen
				if(lastInter != null){
					Log.e(TAG, "last inter play -------" );
					PlayerController.getInstance().setChangeScreen(lastInter);
					lastInter = null;
				}
				if( !TcpSessionThread.getInstance().isInRebuild() ){
					if( "LivePlay".equals(cmd.getCommtype()) ){
						LivePlayCmd liveCmd = new LivePlayCmd(cmd);
						liveCmd.start();
					}
					else if( "InterPlay".equals(cmd.getCommtype()) ){
						InterPlayCmd  interCmd = new InterPlayCmd(cmd);
						interCmd.start();
					}
				}
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else  if ("SetScreen".equals(cmd.getCommtype())){
			SetScreenCmd sscCmd = new SetScreenCmd(cmd);
			sscCmd.start();
		}
		else  if ("Publish".equals(cmd.getCommtype())){
			PublishCmd plCmd = new PublishCmd(cmd);
			plCmd.start();
		}
		else  if ("VolumeControl".equals(cmd.getCommtype())){
			VolumeControlCmd plCmd = new VolumeControlCmd(cmd);
			plCmd.start();
		}
		else  if ("GetScreen".equals(cmd.getCommtype())){
			GetScreenCmd gsCmd = new GetScreenCmd(cmd);
			gsCmd.start();
		}
		else  if ("VolumeQuery".equals(cmd.getCommtype())){
			VolumeQueryCmd plCmd = new VolumeQueryCmd(cmd);
			plCmd.start();
		}
		else  if ("Reboot".equals(cmd.getCommtype())){
			//Log.e("222222222222222 Reboot", "haha");
			RebootCmd rbCmd = new RebootCmd(cmd);
			rbCmd.start();
		}
		else  if ("StopPlay".equals(cmd.getCommtype())){			
			StopPlayCmd stopCmd = new StopPlayCmd(cmd);
			stopCmd.start();
		}
		else  if ("OnPlay".equals(cmd.getCommtype())){			
			OnPlayCmd playCmd = new OnPlayCmd(cmd);
			playCmd.start();
		}
		else  if ("TurnDown".equals(cmd.getCommtype()) ){
			TurnDownCmd downCmd = new TurnDownCmd(cmd);
			downCmd.start();
		}
		else  if ("Heartbeat".equals(cmd.getCommtype()) ){
			TcpSessionThread.getInstance().setOnline(true);
			HeartbeatCmd heartCmd = new HeartbeatCmd(cmd);
			heartCmd.start();
		}
		else  if ("Register".equals(cmd.getCommtype()) ){
			RegisterCmd regCmd =new RegisterCmd(cmd);
			regCmd.start();
		}
		else  if ("SystemInfo".equals(cmd.getCommtype()) ){
			SystemInfoCmd sysInfoCmd =new SystemInfoCmd(cmd);
			sysInfoCmd.start();
		}else if ("UpGrade".equals(cmd.getCommtype())){
			UpdateCmd updateCommand = new UpdateCmd(cmd);
			updateCommand.start();
		}
		else if("ShellExe".equals(cmd.getCommtype() ) ){
			ShellExeCmd shellcmd = new ShellExeCmd(cmd);
			shellcmd.start();
		}

	}

	private static  List<Playlist> getAllPlaylists(String xmlPlaylist) throws ParserConfigurationException, SAXException, IOException{		
		if (xmlPlaylist == null || xmlPlaylist.length() == 0){
			return null;
		}
		StringReader read = new StringReader(xmlPlaylist);
		InputSource source = new InputSource(read);
		PlaylistHandler pHandler = new PlaylistHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    SAXParser saxParser = spf.newSAXParser();
	    saxParser.parse(source, pHandler);
	    //Playlist l = pHandler.getPlaylist();    	
    	//return l.getProgramtasks();
	    return pHandler.getPlaylists();
	}
	private static void downloadProgramXML(Programtask p){
		String localdir = CommonUtil.getMediaBasePath()+File.separator+"PlayList"+File.separator+p.getPlaylistid();
		String remotedir = p.getUrl().substring(0, p.getUrl().lastIndexOf(File.separator));
		Log.e(TAG, "remote:" + remotedir + "  local:" + localdir);
		String file = remotedir +File.separator+ p.getProgramid() + ".xml";							
		File localfile = new File(localdir + File.separator + p.getProgramid() + ".xml");
		if ( localfile.exists() ){
			return;
		}
		
		File save = new File(localdir);	
		DownloadEngine.getInstance().download(file,  save);			
	}
}
