package com.people.lyy.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.DownloadFileRequest;
import com.people.lyy.view.LocusPassWordView;
import com.people.lyy.view.LocusPassWordView.OnCompleteListener;

// 锁屏
public class LockScreenActivity extends BaseActivity implements OnClickListener {
	private LocusPassWordView lpwv;
	private Button forget_psw;
	private int error = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.llll_activity);
		forget_psw = (Button) findViewById(R.id.forget_psw);
		forget_psw.setOnClickListener(this);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (lpwv.verifyPassword(mPassword)) {
					showToast("登陆成功！");
					Intent intent = new Intent(LockScreenActivity.this,
							MainActivity.class);
					// 打开新的Activity
					startActivity(intent);
					finish();
				} else {
					if (error != 3) {
						showToast("密码输入错误,请重新输入");
						lpwv.markError();
						error++;
					} else {
						showToast("密码三次输入错误请重新登录");
						Intent intent = new Intent(LockScreenActivity.this,
								LoginActivity.class);
						// 打开新的Activity
						startActivity(intent);
						finish();
					}
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 如果密码为空,则进入设置密码的界面
		View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
		TextView toastTv = (TextView) findViewById(R.id.login_toast);
		if (lpwv.isPasswordEmpty()) {
			showToast("?????");
			lpwv.setVisibility(View.GONE);
			noSetPassword.setVisibility(View.VISIBLE);
			toastTv.setText("请先绘制手势密码");
			noSetPassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(LockScreenActivity.this,
							LoginActivity.class);
					// 打开新的Activity
					startActivity(intent);
					finish();
				}

			});
		} else {
			toastTv.setText("请输入手势密码");
			lpwv.setVisibility(View.VISIBLE);
			noSetPassword.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	// 返回键的处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 此处写处理的事件
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		this.goLoginActivity();

	}

	public void goLoginActivity() {
		Intent intent = new Intent(LockScreenActivity.this, LoginActivity.class);
		startActivity(intent);
		LockScreenActivity.this.finish();
	}

	public class DownloadAPKTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			download();
			return null;
		}

	}

	private void download() {
		Looper.prepare();
		DownloadFileRequest.sharedInstance().downloadAndOpen(
				this,
				"http://111.198.29.38:6443/client/"
						+ ApplicationEnvironment.getInstance().getPreferences()
								.getString(Constants.kUSERNAME, "")
						+ "/sotp.apk", "download.apk");
	}

}
