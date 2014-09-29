package com.people.lyy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DemoReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String str = intent.getStringExtra("key");
		Log.e("===", "===:"+str);
		
		
	}

}
