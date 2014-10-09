package com.people.lyy.activity;

import java.util.HashMap;

import com.people.lyy.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SuccessActivity extends BaseActivity {
	private Button btn_over = null;
	private TextView tv_cardcode, tv_balance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		initView();
		setView();

	}

	public void initView() {
		tv_cardcode = (TextView) findViewById(R.id.tv_cardcode);
		tv_balance = (TextView) findViewById(R.id.tv_balance);
		btn_over = (Button) findViewById(R.id.btn_over);
	}

	public void setView() {
		HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getSerializableExtra("result");
		tv_cardcode.setText(map.get("card"));
		tv_balance.setText(map.get("money"));
		
		btn_over.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				finish();
			}
			return true;
		}
		return false;
	}
	
}
