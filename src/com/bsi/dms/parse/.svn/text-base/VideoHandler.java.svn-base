package com.bsi.dms.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.bsi.dms.bean.ProgramVideo;

public class VideoHandler  extends DefaultHandler{
	private static final String TAG = "VideoHandler"; 
	private String perTag ;
	private ProgramVideo target = null;
	private ProgramVideo video = null;
	
	public void startDocument() throws SAXException {
	
		//Log.i(TAG , "***startDocument()***");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("Content".equals(localName)){
			for ( int i = 0; i < attributes.getLength(); i++ ) {
				String att = attributes.getLocalName(i);								
				if(att.equals("materialtype")){
					if("Video".equals(attributes.getValue(i)) ){
						video = new ProgramVideo();
					}
				}
			}
		}		
		perTag = localName;
		//Log.i(TAG , qName+"***startElement()***");
	}
	
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length).trim();
		if(video == null ){
			return;
		}	  
	    
	    if ("top".equals(perTag)){
	    	video.setTop(data);
	    }else if("left".equals(perTag)){
	    	video.setLeft(data);
		}
	    else if("width".equals(perTag)){
	    	video.setWidth(data);
		}
	    else if("height".equals(perTag)){
	    	video.setHeight(data);
		}	
	    
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//Log.i(TAG , qName+"***endElement()***");	
		if("Style".equals(localName) && (video != null) ){
			Log.e(TAG, video.toString() );
			target = video;
			video = null;
		}		
		perTag = null;
	}

	public void endDocument() throws SAXException {
		//Log.i(TAG , "***endDocument()***");
	}

	public ProgramVideo getVideo() {
		return video;
	}

	public void setVideo(ProgramVideo video) {
		this.video = video;
	}

	public ProgramVideo getTarget() {
		return target;
	}

	public void setTarget(ProgramVideo target) {
		this.target = target;
	}
}
