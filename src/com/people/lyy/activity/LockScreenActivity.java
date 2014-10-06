package com.people.lyy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.view.GestureLockView;
import com.people.lyy.view.GestureLockView.OnGestureFinishListener;

// 锁屏
public class LockScreenActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);

		Button btn_forget = (Button) findViewById(R.id.btn_forget);
		btn_forget.setOnClickListener(this);

		GestureLockView gv = (GestureLockView) findViewById(R.id.gv);
		gv.setKey(ApplicationEnvironment.getInstance().getPreferences().getString(Constants.kLOCKKEY, "")); // Z 字型
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success) {
				if (success) {
					Intent intent = new Intent(BaseActivity.getTopActivity(), MainActivity.class);
					BaseActivity.getTopActivity().startActivity(intent);
					LockScreenActivity.this.finish();
				}
			}
		});

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
}
