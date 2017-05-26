package com.bsi.dms.player.cmd;

import android.text.TextUtils;
import android.util.Log;

import com.bsi.dms.bean.Command;
import com.bsi.dms.config.PlayerConst;
import com.bsi.dms.update.UpdateDetail;
import com.bsi.dms.update.UpdateManager;
import com.bsi.dms.update.UpdateManagerImpl;
import com.bsi.dms.utils.CommonUtil;

public class UpdateCmd extends Command {
	private static final String UPDATE_TAG = "UPDATE";
	private UpdateDetail mUpdateDetail;

	public UpdateCmd(Command cmd) {
		super(cmd.getCommtype(), cmd.getPlayerid(), cmd.getTaskno(), cmd
				.getValue(), cmd.getData());
	}

	private void parseUpdateDetail(String value) {
		mUpdateDetail = new UpdateDetail();
		if (TextUtils.isEmpty(value)) {
			mUpdateDetail = null;
			return;
		}
		String[] values = value.split(PlayerConst.CMD_VALUE_SPLITOR);
		String type = values[0];
		mUpdateDetail.setInstallType(type);
		if (type.equals(UpdateDetail.UPDATE_TYPE_TIMED)) {
			if (values.length < 2) {
				Log.e(UPDATE_TAG,
						"read install type == UPDATE_TYPE_TIMED,install time null!");
				mUpdateDetail = null;
				return;
			}
			mUpdateDetail.setInstallTime(values[1]);
		}
	}

	@Override
	public void run() {
		super.run();
		UpdateManager updateManager = UpdateManagerImpl.getInstance();
		updateManager.fetchAndUpdate();
		String playerId = getParam("playerid");
		String taskNo = getParam("taskno");
		CommonUtil.reportCommandAck(playerId, taskNo);
	}

}
