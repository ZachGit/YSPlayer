package com.bsi.dms.parse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.bsi.dms.bean.Command;


public class CommandHandler extends DefaultHandler{
	private static final String TAG = "CommandHandler"; 
	private List<Command> commands;
	private String perTag ;//通过此变量，记录前一个标签的名称
	Command command;//记录当前Person
	
	public List<Command> getCommands() {
		return commands;
	}
	
	//适合在此事件中触发初始化行为。
	public void startDocument() throws SAXException {
		commands = new ArrayList<Command>();
		//Log.i(TAG , "***startDocument()***");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("Command".equals(localName)){
			command = new Command();
			for ( int i = 0; i < attributes.getLength(); i++ ) {
				String att = attributes.getLocalName(i);				
				//Log.i(TAG ,"attributeName:" + attributes.getLocalName(i)
				//		+ "_attribute_Value:" + attributes.getValue(i));
				
				if(att.equals("CommType")){
					command.setCommtype(attributes.getValue(i));					
				}
				else if(att.equals("PlayerID")){
					command.setPlayerid(attributes.getValue(i));	
				}
				else if (att.equals("TaskNO")){
					command.setTaskno(attributes.getValue(i));
				}			
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
	    if("Value".equals(perTag)){
				command.setValue(data);
		}else if("Data".equals(perTag)){
				command.setData(data);
		}
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//Log.i(TAG , qName+"***endElement()***");
		if("Command".equals(localName)){
			commands.add(command);
			command = null;
		}
		perTag = null;
	}

	public void endDocument() throws SAXException {
		//Log.i(TAG , "***endDocument()***");
	}

}
