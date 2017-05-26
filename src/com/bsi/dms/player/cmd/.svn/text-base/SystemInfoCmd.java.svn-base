package com.bsi.dms.player.cmd;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.player.TcpSessionThread;
import com.bsi.dms.utils.CommonUtil;
import com.bsi.dms.xmlcreate.XMLTaskCreate;

public class SystemInfoCmd extends Command {
	XMLTaskCreate xmlCreate = null;

	public SystemInfoCmd(Command cmd) {
		super(cmd.getCommtype(), cmd.getPlayerid(), cmd.getTaskno(), cmd
				.getValue(), cmd.getData());
	}

	@Override
	public void run() {
		super.run();

		try {
			xmlCreate = new XMLTaskCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String playerid = PlayerApplication.getInstance().sysconfig.getPlayid();
		// cpu
		String cpuPercent = CommonUtil.readCpuUsagePercent();
		// mem
		long mem = PlayerApplication.getInstance().getMemoryPercent();
		// disk
		long disk = CommonUtil.getSDCardPercent();
		String value = cpuPercent + "|" + mem + "%|" + disk + "%";
		String sysinfo = xmlCreate.createXml("SystemInfo", playerid, "", value,
				"");

		TcpSessionThread.getInstance().sendString(sysinfo);

	}

}
