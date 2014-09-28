package com.people.lyy.activity;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.sqlite.DataDao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity implements OnClickListener {
	private DataDao dao = null;
	private LinearLayout lay_consume, lay_binding, lay_gesture = null;
	public static final int FROM_SETTINGACTIVITY = 1;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(Constants.LOGGED, true);
		editor.commit();
		
		initview();
		ApplicationEnvironment.getInstance().getPreferences().edit();
		if (!dao.find(Constants.LOGGED)) {
			dao.add(Constants.LOGGED, 1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lay_consume:
			Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
			startActivity(intent);
			break;

		case R.id.lay_binding:
			Intent intent0 = new Intent(MainActivity.this, BindActivity.class);
			startActivity(intent0);
			break;

		case R.id.lay_gesture:
			Intent intent1 = new Intent(MainActivity.this,
					LockScreenSettingActivity.class);
			MainActivity.this.startActivity(intent1);
			break;

		default:
			break;
		}

	}

	public void initview() {
		dao = new DataDao(getApplication());
		lay_consume = (LinearLayout) findViewById(R.id.lay_consume);
		lay_consume.setOnClickListener(this);
		lay_binding = (LinearLayout) findViewById(R.id.lay_binding);
		lay_binding.setOnClickListener(this);
		lay_gesture = (LinearLayout) findViewById(R.id.lay_gesture);
		lay_gesture.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				exitApp();
			}
			return true;
		}
		return false;
	}

	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			showToast("再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			for (BaseActivity activity : BaseActivity.getAllActiveActivity()) {
				activity.finish();
			}
		}
	}
}