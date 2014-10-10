package com.people.lyy.activity;

import com.people.lyy.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmOrderActivity extends BaseActivity implements OnClickListener{
	private TextView tv_ordercode ,tv_amount = null;
	private EditText et_psw = null;
	private Button btn_confirm = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmorder);
		
		initView();
	}
	
	private void initView(){
		tv_ordercode = (TextView) findViewById(R.id.tv_ordercode);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
		et_psw = (EditText) findViewById(R.id.et_psw);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			
			break;

		default:
			break;
		}		
	}

}
