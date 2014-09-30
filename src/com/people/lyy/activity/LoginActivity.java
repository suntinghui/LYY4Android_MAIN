package com.people.lyy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.R;

import com.people.lyy.client.TransferRequestTag;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
	private Button btn_login,btn_register = null;
	private long exitTime = 0;
	// private SharedPreferences preferences = ApplicationEnvironment
	// .getInstance().getPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		logoImageView = (ImageView) this.findViewById(R.id.logoImageView);
		Animation myAnimation = AnimationUtils.loadAnimation(this,
				R.anim.login_logo_anim);
		logoImageView.startAnimation(myAnimation);

		usernameEdit = (EditText) this.findViewById(R.id.et_user);
		// usernameEdit.setText(ApplicationEnvironment.getInstance()
		// .getPreferences(this).getString(Constants.kUSERNAME, ""));
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
				if(isAvilible(this, "com.example.photoactivity")){					
					login();
				}else{
					showToast("请先注册");
				}
			}
			break;
		case R.id.btn_register:
			Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
			startActivityForResult(intent,0);
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
		tempMap.put("TRANCODE", "199002");
		tempMap.put("PHONENUMBER", usernameEdit.getText().toString().trim());
		tempMap.put("PASSWORD", passwordEdit.getText().toString().trim());
		tempMap.put("PCSIM", "不能获取");

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Login,
				tempMap, getLoginHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
				"正在登录请稍候...", new LKHttpRequestQueueDone() {
					// 执行的是登录以后操作的东西
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
				// 启动超时退出服务
				Intent intent = new Intent(BaseActivity.getTopActivity(),
						TimeoutService.class);
				BaseActivity.getTopActivity().startService(intent);
				// 就是这四行代码不能登陆成功
				// HashMap<String, Object> map = (HashMap<String, Object>) obj;
				// String RSPCOD = (String) map.get("RSPCOD");
				// String RSPMSG = (String) map.get("RSPMSG");
				// String PHONENUMBER = (String) map.get("PHONENUMBER");
				// // Constants.APPTOKEN = (String) map.get("APPTOKEN");
				//
				// if (RSPCOD.equals("00")) {
				// Editor editor = ApplicationEnvironment.getInstance()
				// .getPreferences(LoginActivity.this).edit();
				// editor.putString(Constants.kUSERNAME, PHONENUMBER);
				// editor.putString(Constants.kPASSWORD, passwordEdit
				// .getText().toString().trim());
				// editor.commit();

				Intent intent0 = new Intent(LoginActivity.this,
						LockScreenSettingActivity.class);
				LoginActivity.this.startActivity(intent0);

				// } else {
				// Toast.makeText(LoginActivity.this, RSPMSG,
				// Toast.LENGTH_SHORT).show();
				// }

			}

		};
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				this.exitApp();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	//用于判断程序是否安装
	private boolean isAvilible(Context context, String packageName){ 
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager 
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息 
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名 
       //从pinfo中将包名字逐一取出，压入pName list中 
            if(pinfo != null){ 
            for(int i = 0; i < pinfo.size(); i++){ 
                String pn = pinfo.get(i).packageName; 
                pName.add(pn); 
            } 
        } 
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE 
  } 
}
