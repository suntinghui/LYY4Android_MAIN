package com.people.lyy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DemoReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("SOTP", "收到回应。。。");
		
		String type = intent.getStringExtra("SOTP");
		if (type.equals("genTOKEN")){
			Log.e("SOTP", intent.getStringExtra("key").trim());
			
			Intent intent0 = new Intent(context, AccountsInfoActivity.class);
			intent0.putExtra("token", intent.getStringExtra("key").trim());
			intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent0);
		}
		
		
	}

}
