package com.bsi.dms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IBsMessage_Receiver extends BroadcastReceiver {

	    // 鎺ユ敹娑堟伅
	    public static final String IMESSAGE_SERVICE_ACTION = "com.bsi.dms.ation.SERVICE_ACTION";
				
		@Override
		public void onReceive(Context context, Intent intent) {
			
			//ActivityUtil.startApp(context, "鐢佃瀹�2.0");
		}

}
