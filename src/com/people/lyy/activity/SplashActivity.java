package com.people.lyy.activity;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.view.LKAlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TextView tv_tips = (TextView) findViewById(R.id.tv_tips);
		PackageManager pm = this.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			tv_tips.setText("众易付(zPOS) V" + pi.versionName);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new SplashTask().execute();
	}

	class SplashTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				/***
				 * // 利用闪屏界面初始化系统 long startTime = System.currentTimeMillis();
				 * //ApplicationEnvironment.getInstance().initialize(
				 * SplashActivity.this.getApplication()); long endTime =
				 * System.currentTimeMillis(); long cashTime = endTime -
				 * startTime; Log.e("Splash Time", String.valueOf(cashTime));
				 * 
				 * // 从效果上保证最少要保持闪屏界面不少于1500ms // 因为现在有取地址会占用一些时间，所以就不加额外等待时间了。
				 * if (cashTime < 1500){ Thread.sleep(1500 - cashTime); }
				 ****/

				Thread.sleep(1500);

				return null;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			if (ApplicationEnvironment.getInstance().checkNetworkAvailable()) {
				Intent intent = new Intent(SplashActivity.this,
						LoginActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();

			} else {
				LKAlertDialog dialog = new LKAlertDialog(
						BaseActivity.getTopActivity());
				dialog.setTitle("提示");
				dialog.setCancelable(false);
				dialog.setMessage(SplashActivity.this.getResources().getString(
						R.string.noNetTips));
				dialog.setPositiveButton("设置网络",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);

								finish();
							}
						});
				dialog.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});

				dialog.create().show();
			}
		}

	}

}
