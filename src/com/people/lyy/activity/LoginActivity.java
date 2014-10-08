package com.people.lyy.activity;

import java.util.HashMap;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.DownloadFileRequest;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.jababean.AccountInfo;
import com.people.lyy.util.ActivityUtil;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private ImageView logoImageView = null;
	private EditText usernameEdit = null;
	private EditText passwordEdit = null;
	private Button btn_login, btn_register = null;

	private String downloadAPKURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		logoImageView = (ImageView) this.findViewById(R.id.logoImageView);
		Animation myAnimation = AnimationUtils.loadAnimation(this,
				R.anim.login_logo_anim);
		logoImageView.startAnimation(myAnimation);

		usernameEdit = (EditText) this.findViewById(R.id.et_user);
		usernameEdit.setText(ApplicationEnvironment.getInstance()
				.getPreferences(this).getString(Constants.kUSERNAME, ""));
		usernameEdit.setSelection(usernameEdit.getText().toString().length());
		passwordEdit = (EditText) this.findViewById(R.id.et_pwd);

		btn_login = (Button) this.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		btn_register = (Button) this.findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if (checkValue()) {
				login();
			}
			break;
		case R.id.btn_register:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}

	}

	private boolean checkValue() {
		if ("".equals(usernameEdit.getText().toString().trim())) {
			Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
			return false;
		} else if ("".equals(passwordEdit.getText().toString().trim())) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// 登录
	private void login() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("username", usernameEdit.getText().toString().trim());
		tempMap.put("password", passwordEdit.getText().toString().trim());

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Login,
				tempMap, getLoginHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
				"正在登录请稍候...", new LKHttpRequestQueueDone() {
					@Override
					public void onComplete() {
						super.onComplete();
						Editor editor = ApplicationEnvironment.getInstance()
								.getPreferences().edit();
						editor.putString(Constants.kUSERNAME, usernameEdit
								.getText().toString().trim());
						editor.putString(Constants.kPASSWORD, passwordEdit
								.getText().toString().trim());
						editor.commit();

						passwordEdit.setText("");
					}
				});
	}

	private LKAsyncHttpResponseHandler getLoginHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> map = (HashMap<String, String>) obj;

				String rt = map.get("ret");
				if (rt.equals("0")) { // 登录成功
					String url = map.get("url");
					String version = map.get("version");
					LoginActivity.this
							.hideDialog(BaseActivity.ADPROGRESS_DIALOG);

					if (ApplicationEnvironment.getInstance().getPreferences()
							.getInt(Constants.kVERSION, 0) == Integer
							.parseInt(version)
							&& ActivityUtil.isAvilible(LoginActivity.this,
									Constants.SOTPPACKET)) {
						Intent intent = new Intent(LoginActivity.this,
								LockScreenSettingActivity.class);
						LoginActivity.this.startActivity(intent);

					} else {

						downloadAPKURL = Constants.IP + url;

						new DownloadAPKTask().execute();

						Editor editor = ApplicationEnvironment.getInstance()
								.getPreferences().edit();
						editor.putInt(Constants.kVERSION,
								Integer.parseInt(version));
						editor.commit();

						Intent intent = new Intent(LoginActivity.this,
								LockScreenSettingActivity.class);
						LoginActivity.this.startActivity(intent);
					}

					getAccounts();

				} else if (rt.equals("1")) { // 参数不合法
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG,
							"参数不合法！");
				} else if (rt.equals("3")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG,
							"用户名错误！");
				} else if (rt.equals("4")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG,
							"密码错误！");
				} else if (rt.equals("5")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG,
							"文件不存在！");
				}
			}

		};
	}

	private void getAccounts() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("username", ApplicationEnvironment.getInstance()
				.getPreferences().getString(Constants.kUSERNAME, ""));
		tempMap.put("password", ApplicationEnvironment.getInstance()
				.getPreferences().getString(Constants.kPASSWORD, ""));

		LKHttpRequest req1 = new LKHttpRequest(
				TransferRequestTag.getAccountStr, tempMap, getAccountsHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
				"正在加载数据请稍候。。。", new LKHttpRequestQueueDone() {
					@Override
					public void onComplete() {
						super.onComplete();

						new DownloadAPKTask().execute();

						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						LoginActivity.this.startActivity(intent);
					}
				});

	}

	public LKAsyncHttpResponseHandler getAccountsHandler() {

		return new LKAsyncHttpResponseHandler() {
			@Override
			public void successAction(Object obj) {
				Editor editor = ApplicationEnvironment.getInstance()
						.getPreferences().edit();
				editor.putString(Constants.kACCOUNTLIST, (String) obj);
				editor.commit();
			}
		};

	}

	public void onBackPressed() {
		super.onBackPressed();

		finish();
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
		DownloadFileRequest.sharedInstance().downloadAndOpen(this,
				downloadAPKURL, "download.apk");
	}

	public class SleepTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

		}
	}
}
