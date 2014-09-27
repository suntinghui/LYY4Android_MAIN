package com.people.lyy.activity;

import com.people.lyy.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BindActivity extends BaseActivity implements OnClickListener {
	private EditText et_card_code, et_card_pwd = null;
	private Button btn_back, btn_confirm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind);
		initview();
	}

	public void initview() {
		et_card_code = (EditText) findViewById(R.id.et_card_code);
		et_card_pwd = (EditText) findViewById(R.id.et_card_pwd);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_confirm:
			if(checkValue()){
				//银行帐号和密码不为空时进行的操作
			}
			break;
		default:
			break;
		}

	}
	private Boolean checkValue(){
		if(et_card_code.getText().toString().length() == 0){
			showToast("银行卡号不能为空");
			return false;
		}		
		if(et_card_pwd.getText().toString().length() == 0){
			showToast("密码不能为空");
			return false;
		}
		return true;
	}
}
