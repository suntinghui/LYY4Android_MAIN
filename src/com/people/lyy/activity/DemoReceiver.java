package com.people.lyy.activity;

import com.people.lyy.client.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DemoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("SOTP", "收到回应。。。");

		String type = intent.getStringExtra("SOTP");
		if (type.equals("genTOKEN")) {
			Log.e("SOTP", intent.getStringExtra("key").trim());

			if (Constants.GENTOKEN_ONLINE) {
				Intent intent0 = new Intent(context,
						OnlineAccountsInfoActivity.class);
				intent0.putExtra("token", intent.getStringExtra("key").trim());
				intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent0);

			} else {
				Intent intent1 = new Intent(context, AccountsInfoActivity.class);
				intent1.putExtra("token", intent.getStringExtra("key").trim());
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent1);
			}

		}

	}

}
