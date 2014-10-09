package com.people.lyy.activity;

import com.people.lyy.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OnlineShopActivity extends BaseActivity implements OnClickListener {
	private Button btn_scan = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onlineshop);

		initView();
	}

	private void initView() {
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_scan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan:
			Intent intent = new Intent(OnlineShopActivity.this,
					CaptureActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
