package com.people.lyy.activity;

import com.people.lyy.R;
import com.people.lyy.client.DownloadFileRequest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterActivity extends BaseActivity implements OnClickListener{
	private Button btn_back,btn_confirm = null;
	private String downloadAPKURL = "http://gdown.baidu.com/data/wisegame/b17ab04ff4456145/anzhuoshichang_83.apk";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initview();
		setview();
	}
	
	public void initview(){
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}
	
	public void setview(){
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
			download();
			break;
		default:
			break;
		}
		
	}
	
	public class downTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected Object doInBackground(Object... params) {
			download();
			return null;
		}
	} 
	// 下载的类
		private void download() {
				DownloadFileRequest.sharedInstance().downloadAndOpen(RegisterActivity.this,
						downloadAPKURL, "download.apk");
			
		}
}
