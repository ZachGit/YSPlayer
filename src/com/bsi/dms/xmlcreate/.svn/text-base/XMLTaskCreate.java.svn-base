package com.bsi.dms.xmlcreate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bsi.dms.bean.Programtask;
import com.bsi.dms.bean.Syscfg;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.player.PlayerController;
import com.bsi.dms.utils.CommonUtil;

public class XMLTaskCreate {
	DocumentBuilderFactory dbf  = null;
	DocumentBuilder db = null;
	Document doc = null;
	TransformerFactory tff = null;
	Transformer tf = null;
	Source in = null;
	Result out = null;
	String xmlpath = null;
	String strXML = null;
	
	public XMLTaskCreate()throws Exception
	{
		
		dbf = DocumentBuilderFactory.newInstance();//实例化工厂类
		dbf.setValidating(false);//不进行有效性检查
		dbf.setNamespaceAware(true);
		
		db = dbf.newDocumentBuilder();//实例化DocumentBuilder类
		
		doc = db.newDocument();//实例化Document类
		
		tff = TransformerFactory.newInstance();
		tf = tff.newTransformer();
		
		in = new DOMSource(doc);
	}

/*=====================================================
 *============生成xml文件
 *=====================================================*/
	public void ProuduceXml(String axmlpath, String strCommType, String strPlayerID, String strTaskNo, String strValue, String strData)
	{
		try
		{
		Element heartbeat = doc.createElement("Command");//生产根元素students		
		doc.appendChild(heartbeat);//将根元素添加到根节点后面
//		
//		//下面3个字符串需从终端获取，暂时指定值
//		strCommType = "Reboot";
//		strPlayerID = "123456";
//		strTaskNo = "RP20130220";
//		
//		//需要进行获取，根据不同的命令
//		strValue = "Test Value";
//		strData = "Test data";
		
		heartbeat.setAttribute("CommType", strCommType);
		heartbeat.setAttribute("PlayerID", strPlayerID);
		heartbeat.setAttribute("TaskNO", strTaskNo);
		
		Element Value = doc.createElement("Value");//创建Value子元素
		Value.appendChild(doc.createTextNode(strValue));//在Value元素后添加文本节点
		heartbeat.appendChild(Value);//添加heartbeat的子元素Value
		
		Element data = doc.createElement("Data");//创建data子元素
		data.appendChild(doc.createTextNode(strData));//在data元素后添加文本节点
		heartbeat.appendChild(data);//添加heartbeat的子元素data
		
		xmlpath = axmlpath;
		out=new StreamResult(new FileOutputStream(xmlpath + "tr.xml"));//生成输出源
		tf.transform(in,out); 
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public String createXml(String strCommType, String strPlayerID, String strTaskNo, String strValue, String strData)
	{
		try
		{
		Element heartbeat = doc.createElement("Command");//生产根元素students		
		doc.appendChild(heartbeat);//将根元素添加到根节点后面

		
		heartbeat.setAttribute("CommType", strCommType);
		heartbeat.setAttribute("PlayerID", strPlayerID);
		heartbeat.setAttribute("TaskNO", strTaskNo);
		
		Element Value = doc.createElement("Value");//创建Value子元素
		Value.appendChild(doc.createTextNode(strValue));//在Value元素后添加文本节点
		heartbeat.appendChild(Value);//添加heartbeat的子元素Value
		
		Element data = doc.createElement("Data");//创建data子元素
		data.appendChild(doc.createTextNode(strData));//在data元素后添加文本节点
		heartbeat.appendChild(data);//添加heartbeat的子元素data
		
		//out=new StreamResult(new FileOutputStream("c:/" + "qqtr.xml"));//生成输出源
		//tf.transform(in,out); 
		
		ByteArrayOutputStream  bos  =  new  ByteArrayOutputStream(); 
		tf.transform(new DOMSource(doc), new StreamResult(bos));
		
		strXML = bos.toString();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return strXML;
	}

	public String createConfigXml(String ip, String port, String playid,
			String hbtime, String baseurl, String equipmentId,
			String serverVirtualDir) {

		if(ip == null || port == null || PlayerApplication.getInstance().sysconfig.getMac()==null ){
			return null;
		}
		try {
			Element appset = doc.createElement("appSettings");// 生成根元素appSettings
			doc.appendChild(appset);// 将根元素添加到根节点后面

			Element serverip = doc.createElement("serverip");// 创建serverip子元素
			serverip.appendChild(doc.createTextNode(ip));// 在serverip元素后添加文本节点
			appset.appendChild(serverip);// 添加appSettings的子元素serverip

			Element serverport = doc.createElement("serverport");// 创建serverport子元素
			serverport.appendChild(doc.createTextNode(port));// 在serverport元素后添加文本节点
			appset.appendChild(serverport);// 添加appSettings的子元素serverport

			Element playID = doc.createElement("playid");// 创建serverip子元素
			playID.appendChild(doc.createTextNode(playid));// 在serverip元素后添加文本节点
			appset.appendChild(playID);// 添加appSettings的子元素serverip

			Element heartbeat = doc.createElement("heartbeattime");// 创建serverip子元素
			heartbeat.appendChild(doc.createTextNode(hbtime));// 在serverip元素后添加文本节点
			appset.appendChild(heartbeat);// 添加appSettings的子元素serverip

			Element base = doc.createElement("baseurl");// 创建server base url
			base.appendChild(doc.createTextNode(baseurl));//
			appset.appendChild(base);//

			// add mac
			// PlayerApplication.getInstance().sysconfig.getMac()
			Element mac = doc.createElement("mac");// 创建serverip子元素
			String macStr = PlayerApplication.getInstance().sysconfig.getMac();
			if(macStr == null ){
				macStr = "";
			}
			
			mac.appendChild(doc.createTextNode(macStr));//
			appset.appendChild(mac);// 添加appSettings的子元素serverip

			Element equipmentIdElmt = doc.createElement("equipmentId");
			equipmentIdElmt.appendChild(doc.createTextNode(PlayerApplication
					.getInstance().sysconfig.getEquipmentId()));//
			appset.appendChild(equipmentIdElmt);

			Element serverVirtualDirlmt = doc.createElement("serverVirtualDir");
			serverVirtualDirlmt.appendChild(doc.createTextNode(serverVirtualDir));//
			appset.appendChild(serverVirtualDirlmt);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			tf.transform(new DOMSource(doc), new StreamResult(bos));
			
			strXML = bos.toString();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return strXML;
	}
	
	public String createRegisterXml(){
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String cpu = "ARMv7 Processor rev 4 (v7l)";
		String disk = "MemTotal:873136kB";
		String cpuType = "ARM";
		String osType = "Android";
		String resolution = "1280*720";
		
		Syscfg syscfg= PlayerApplication.getInstance().sysconfig;

		
		String mac = null  ;
		if(syscfg != null){
			mac = syscfg.getMac();			
		}
		
		if(mac == null || "".equals( mac )) {
			mac = CommonUtil.getLocalMacAddress();
			if(mac != null ){
				syscfg.setMac(mac);
			}
			else{
				mac = "";
			}
		}
	
	
		//String mac = "50:46:5D:67:5A:C3";
		//String mac = "50:46:5D:67:5A:C8";
		String value = mac + "|" + cpu +"|" + disk + "|" + cpuType +"|" + osType +"|" + resolution;	
		
		//String value = "50:46:5D:67:5A:C8|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device";
		//String value = "50:46:5D:67:5A:C3|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device";
		//String value = "CC:BB:DD:BF:32:92|178BFBFF00300F10|ST500DM0 02-1BD142 SATA Disk Device";
		return createXml("Register", "", "", value, "");		
	}
	
	public String createHeartbeatXml(){		
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		String taskno = "";
		Programtask cur = PlayerController.getInstance().getCurrentPlay();
        if (cur != null){
        	taskno = cur.getTaskno();
        }
		return createXml("Heartbeat", playerid, taskno, "1", "");	
	}
	
}
