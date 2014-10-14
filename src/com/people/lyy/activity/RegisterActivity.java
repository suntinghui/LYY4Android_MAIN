package com.people.lyy.activity;

import java.util.HashMap;
import com.people.lyy.R;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.view.LKAlertDialog;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_confirm = null;

	private EditText usernameEdit = null;
	private EditText passwordEdit = null;
	private String szImei = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initview();
		setview();
	}

	public void initview() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		szImei = TelephonyMgr.getDeviceId();
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		usernameEdit = (EditText) findViewById(R.id.et_username);
		passwordEdit = (EditText) findViewById(R.id.et_pwd);
	}

	public void setview() {
		btn_back.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_confirm:
			if (passwordEdit.getText().toString().length() == 0) {
				showToast("密码不能为空");
			} else if (isMobileNO(usernameEdit.getText().toString())) {
				register();
			} else {
				showToast("请输入手机号作为用户名");

			}
			break;
		default:
			break;
		}

	}

	private void register() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("username", usernameEdit.getText().toString().trim());
		tempMap.put("password", passwordEdit.getText().toString().trim());
		tempMap.put("imei", szImei);
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SignUp,
				tempMap, getLoginHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在提交数据...",
				new LKHttpRequestQueueDone() {
					@Override
					public void onComplete() {
						super.onComplete();
					}
				});
	}

	private LKAsyncHttpResponseHandler getLoginHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> map = (HashMap<String, String>) obj;
				BaseActivity.getTopActivity().hideDialog(ADPROGRESS_DIALOG);
				String rt = map.get("ret");
				if (rt.equals("0")) { // 注册成功
					showDialog("注册成功！", true);
				} else if (rt.equals("1")) { // 参数不合法
					showDialog("参数不合法！", false);
				} else if (rt.equals("2")) { // 该用户名已经注册
					showDialog("该用户名已经注册！", false);
				} else {
					showDialog("未知错误！", false);
				}
			}

		};
	}

	private void showDialog(String msg, final boolean shouldFinish) {
		LKAlertDialog alertDialog = new LKAlertDialog(RegisterActivity.this);
		alertDialog.setTitle("提示");
		alertDialog.setMessage(msg);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						if (shouldFinish)
							RegisterActivity.this.finish();
					}
				});
		alertDialog.create().show();
	}

	public static boolean isMobileNO(String mobiles) {

		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
}
