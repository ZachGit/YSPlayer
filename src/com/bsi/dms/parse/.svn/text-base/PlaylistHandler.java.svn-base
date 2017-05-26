package com.bsi.dms.parse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.bean.Playlist;
import com.bsi.dms.bean.Programtask;

public class PlaylistHandler extends DefaultHandler {

	private static final String TAG = "PlaylistHandler"; 
	private List<Programtask> programtasks;
	private String perTag ;
	private Playlist playlist;
	private List<Playlist> playlists;
	private Programtask programtask=null;
	private List<String> reslist;
	private String  resitem;
	
	
	public Playlist getPlaylist() {
		return playlist;
	}
	
	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = playlists;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub		
		//super.startDocument();
		playlists = new ArrayList<Playlist>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
//		super.startElement(uri, localName, qName, attributes);
		
		if("PlayList".equals(localName)){
			playlist = new Playlist();
			programtasks = new ArrayList<Programtask>();
			
			for ( int i = 0; i < attributes.getLength(); i++ ) {
				String att = attributes.getLocalName(i);				
				//Log.i(TAG ,"attributeName:" + attributes.getLocalName(i)
				//		+ "_attribute_Value:" + attributes.getValue(i));
				
				if(att.equals("PlayListID")){
					playlist.setPlaylistid(attributes.getValue(i));					
				}
				else if(att.equals("PlayListName")){
					playlist.setPlaylistname(attributes.getValue(i));					
				}
				else if(att.equals("SiteID")){
					playlist.setSiteid(attributes.getValue(i));					
				}
				else if(att.equals("plantype")){
					playlist.setPlantype(attributes.getValue(i));					
				}
			}
		}
		else if("Programtask".equals(localName)){
			programtask = new Programtask();
			for ( int i = 0; i < attributes.getLength(); i++ ) {
				//Log.i(TAG ,"attributeName:" + attributes.getLocalName(i)
				//		+ "_attribute_Value:" + attributes.getValue(i));
				String att = attributes.getLocalName(i);
				if(att.equals("ProgramID")){
					programtask.setProgramid(attributes.getValue(i));					
				}
				else if (att.equals("ProgramName")){
					programtask.setProgramname(attributes.getValue(i));					
				}
				else if (att.equals("onlinemode")){
					programtask.setOnlinemode(attributes.getValue(i) );					
				}
			}
		}
		else if("local".equals(localName)){	
			reslist = new  ArrayList<String>();			
			for ( int i = 0; i < attributes.getLength(); i++ ) {
				String att = attributes.getLocalName(i);
				if(att.equals("protocol") && programtask != null){
					programtask.setLocalprotocol(attributes.getValue(i));
				}
				else if(att.equals("ftpserver") && programtask != null){
					programtask.setLocalftpserver(attributes.getValue(i));
				}				
			}
			
		}
		perTag = localName;
		//Log.i(TAG , qName+"***startElement()***");
		
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.characters(ch, start, length);
		String data = new String(ch, start, length).trim();
		if(!"".equals(data.trim())){
	           //Log.i(TAG ,"content: " + data.trim());
	    }

		if("StartDate".equals(perTag)){
			if(programtask != null){
				programtask.setStartdate(data);	
			}
			else if(playlist != null && "loop".equals(playlist.getPlantype()) ){
				playlist.setLoopstartdate( data );				
			}
		}
		else if("EndDate".equals(perTag)){
			if(programtask != null){
				programtask.setEnddate(data);	
			}
			else if(playlist != null && "loop".equals(playlist.getPlantype())){
				playlist.setLoopenddate(data);
			}
		}
		else if("StartTime".equals(perTag)){
			if(programtask != null){
				programtask.setStarttime(data);	
			}
			else if(playlist != null && "loop".equals(playlist.getPlantype()) ){
				playlist.setLoopstarttime(data);
			}
		}		
		else if("EndTime".equals(perTag)){
			if(programtask != null){
				programtask.setEndtime(data);
			}
			else if(playlist != null && "loop".equals(playlist.getPlantype()) ){
				playlist.setLoopendtime(data);
			}
		}
		else if("Week".equals(perTag)){
			if(programtask != null){
				programtask.setWeek(data);
			}
			else if(playlist != null && "loop".equals(playlist.getPlantype()) ){
				playlist.setLoopweek(data);
			}
		}
		else if("CycleIndex".equals(perTag)){
			programtask.setCycleindex(data);		 
		}
		else if("Time".equals(perTag)){
			programtask.setTime(data);		 
		}
		else if("url".equals(perTag)){
			programtask.setUrl(data);		 
		}	
		else if("programurl".equals(perTag)){
			programtask.setLocalprogramurl(data);		 
		}
		else if("res".equals(perTag)){
			reslist.add(data);
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		//super.endElement(uri, localName, qName);
		//Log.i(TAG , qName+"***endElement()***");
		if("Programtask".equals(localName) && (programtask != null) ){
			programtasks.add(programtask);
			Log.e(TAG, programtask.toString() );
			if (programtask.getLocalres() != null){
				Log.e(TAG, programtask.getLocalres().toString() );
			}
			programtask = null;
		}
		else if("PlayList".equals(localName) && (playlist != null)  ){	
			playlist.setProgramtasks(programtasks);
			playlists.add(playlist);
			playlist = null;
		}
		else if("local".equals(localName) && (programtask != null)  ){
			programtask.setLocalres(reslist);	
			Log.e(TAG, reslist.toString() );
			reslist = null;
		}
		perTag = null;
	}
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		//super.endDocument();
		//Log.i(TAG , "***endDocument()***");
	}





	

}

