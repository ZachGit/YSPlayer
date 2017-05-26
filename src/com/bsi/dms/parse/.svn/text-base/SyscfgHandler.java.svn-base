package com.bsi.dms.parse;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.bsi.dms.bean.Syscfg;

public class SyscfgHandler extends DefaultHandler{
	private static final String TAG = "SyscfgHandler"; 
	private List<Syscfg> syscfgs;
	private String perTag ;//通过此变量，记录前一个标签的名称。
	Syscfg syscfg;//记录当前Person
	
	public List<Syscfg> getSyscfgs() {
		return syscfgs;
	}

	//适合在此事件中触发初始化行为。
	public void startDocument() throws SAXException {
		syscfgs = new ArrayList<Syscfg>();
		//Log.i(TAG , "***startDocument()***");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("appSettings".equals(localName)){
			syscfg = new Syscfg();
			for ( int i = 0; i < attributes.getLength(); i++ ) {
			}
		}
		perTag = localName;
		//Log.i(TAG , qName+"***startElement()***");
	}
	
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length).trim();
	    if(!"".equals(data.trim())){
	           //Log.i(TAG ,"content: " + data.trim());
	    }
	    
	    if ("serverip".equals(perTag)){
	    	syscfg.setServerip(data);
	    }else if("serverport".equals(perTag)){
	    	syscfg.setServerport(data);
		}else if("playid".equals(perTag)){
			syscfg.setPlayid(data);
		}else if("heartbeattime".equals(perTag)){
			syscfg.setHeartbeattime(Integer.valueOf(data));
		}else if("baseurl".equals(perTag)){
			syscfg.setBaseurl(data);
		}else if("mac".equals(perTag)){
			syscfg.setMac(data);
		} else if ("equipmentId".equals(perTag)) {
			syscfg.setEquipmentId(data);
		} else if ("serverVirtualDir".equals(perTag)) {
			syscfg.setServerVirtualDir(data);
		}
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//Log.i(TAG , qName+"***endElement()***");
		if("appSettings".equals(localName)){
			syscfgs.add(syscfg);
			syscfg = null;
		}
		perTag = null;
	}

	public void endDocument() throws SAXException {
		//Log.i(TAG , "***endDocument()***");
	}

}
