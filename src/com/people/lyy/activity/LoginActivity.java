package com.people.lyy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.DownloadFileRequest;

import com.people.lyy.client.TransferRequestTag;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
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
	private long exitTime = 0;

	private String downloadAPKURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		logoImageView = (ImageView) this.findViewById(R.id.logoImageView);
		Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.login_logo_anim);
		logoImageView.startAnimation(myAnimation);

		usernameEdit = (EditText) this.findViewById(R.id.et_user);
		usernameEdit.setText(ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
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
			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Login, tempMap, getLoginHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在登录请稍候...", new LKHttpRequestQueueDone() {
			@Override
			public void onComplete() {
				super.onComplete();
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
					if (isAvilible(LoginActivity.this, Constants.SOTPPACKET)) { // 已经安装sotp服务，直接跳转到界面界面
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						LoginActivity.this.startActivity(intent);
					} else { // 没有安装程序，则去下载
						downloadAPKURL = Constants.IP + map.get("url");
						new DownloadAPKTask().execute();
					}
				} else if (rt.equals("1")) { // 参数不合法
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG, "参数不合法！");
				} else if (rt.equals("3")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG, "用户名错误！");
				} else if (rt.equals("4")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG, "密码错误！");
				} else if (rt.equals("5")) {
					LoginActivity.this.showDialog(BaseActivity.MODAL_DIALOG, "文件不存在！");
				}
			}

		};
	}

	public void onBackPressed(){
		super.onBackPressed();
		
		finish();
	}

	// 用于判断程序是否安装
	private boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
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
		DownloadFileRequest.sharedInstance().downloadAndOpen(this, downloadAPKURL, "download.apk");
	}

}
