package com.people.lyy.activity;

import com.people.lyy.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DefeatedActivity extends BaseActivity {
	private Button btn_over = null;
	private TextView msgText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defeated);

		initview();
		setview();
	}

	public void initview() {
		msgText = (TextView) findViewById(R.id.msgText);
		btn_over = (Button) findViewById(R.id.btn_over);
	}

	public void setview() {
		msgText.setText(this.getIntent().getStringExtra("result"));

		btn_over.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				DefeatedActivity.this.finish();
			}
		});

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				setResult(RESULT_OK);
				DefeatedActivity.this.finish();
			}
			return true;
		}
		return false;
	}

}
