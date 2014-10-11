package com.people.lyy.activity;

import java.util.HashMap;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.TransferRequestTag;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmOrderActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_ordercode, tv_amount, tv_time = null;
	private EditText et_psw = null;
	private Button btn_confirm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmorder);

		initView();
	}

	private void initView() {
		tv_ordercode = (TextView) findViewById(R.id.tv_ordercode);
		tv_ordercode.setText("订单号：" + Constants.resultString.substring(0, 10));
		// tv_ordercode.setText("交易流水号："+Constants.resultString.substring(21,
		// 28));

		tv_amount = (TextView) findViewById(R.id.tv_amount);
		tv_amount.setText(Constants.resultString.substring(11, 18));
		// 这里其实是商户名称 竟然没有交易金额
		// tv_amount.setText(Constants.resultString.substring(21, 28));
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setText("交易日期" + Constants.resultString.substring(0, 8));

		et_psw = (EditText) findViewById(R.id.et_psw);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (checkValue()) {
				upLoading();
			}
			break;

		default:
			break;
		}
	}

	private void upLoading() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("token", Constants.resultString);
		tempMap.put("money", this.getIntent().getStringExtra("money"));

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.OnlineShop,
				tempMap, upLoadingHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(null,
				new LKHttpRequestQueueDone() {
					@Override
					public void onComplete() {
						super.onComplete();

					}

				});

	}

	public LKAsyncHttpResponseHandler upLoadingHandler() {

		return new LKAsyncHttpResponseHandler() {
			@Override
			public void successAction(Object obj) {
				HashMap<String, String> resultMap = (HashMap<String, String>) obj;
				String ret = resultMap.get("ret");
				int r = Integer.parseInt(ret);
				if (r == 0) {

					Intent resultIntent = new Intent(ConfirmOrderActivity.this,
							SuccessActivity.class);
					// resultIntent.putExtra("result", resultMap);
					startActivity(resultIntent);

				} else {
					String msg = "未知异常";
					if (r == 10) {
						msg = "用户不存在";
					} else if (r == 11) {
						msg = "二维码超时";
					} else if (r == 12) {
						msg = "余额不足";
					} else if (r == 1) {
						msg = "参数错误";
					} else if (r == 15) {
						msg = "该码已经扫描过了";
					}

					Intent resultIntent = new Intent(ConfirmOrderActivity.this,
							DefeatedActivity.class);
					resultIntent.putExtra("result", msg);
					startActivity(resultIntent);
				}
				ConfirmOrderActivity.this.finish();
			}
		};

	}

	private boolean checkValue() {

		if ("".equals(et_psw.getText().toString().trim())) {
			showToast("请输入密码");
			return false;
		} else if (!ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constants.kPASSWORD, "")
				.equals(et_psw.getText().toString().trim())) {
			showToast("密码错误请重新输入");
			return false;
		} else if (ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constants.kPASSWORD, "")
				.equals(et_psw.getText().toString().trim())) {
			return true;
		}

		return false;
	}
	
//	private void upLoading2() {
//		HashMap<String, Object> tempMap = new HashMap<String, Object>();
//		tempMap.put("token", Constants.resultString);
//		tempMap.put("data", Constants.resultString);
//		tempMap.put("trader", Constants.resultString);
//		tempMap.put("order", Constants.resultString);
//		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.OnlineShop,
//				tempMap, upLoadingHandler2());
//
//		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(null,
//				new LKHttpRequestQueueDone() {
//					@Override
//					public void onComplete() {
//						super.onComplete();
//
//					}
//
//				});
//
//	}
//
//	public LKAsyncHttpResponseHandler upLoadingHandler2() {
//
//		return new LKAsyncHttpResponseHandler() {
//			@Override
//			public void successAction(Object obj) {
//				HashMap<String, String> resultMap = (HashMap<String, String>) obj;
//				String ret = resultMap.get("ret");
//				int r = Integer.parseInt(ret);
//				if (r == 0) {
//
//					Intent resultIntent = new Intent(ConfirmOrderActivity.this,
//							SuccessActivity.class);
//					// resultIntent.putExtra("result", resultMap);
//					startActivity(resultIntent);
//
//				} else {
//					String msg = "未知异常";
//					if (r == 10) {
//						msg = "用户不存在";
//					} else if (r == 11) {
//						msg = "二维码超时";
//					} else if (r == 12) {
//						msg = "余额不足";
//					} else if (r == 1) {
//						msg = "参数错误";
//					} else if (r == 15) {
//						msg = "该码已经扫描过了";
//					}
//
//					Intent resultIntent = new Intent(ConfirmOrderActivity.this,
//							DefeatedActivity.class);
//					resultIntent.putExtra("result", msg);
//					startActivity(resultIntent);
//				}
//				ConfirmOrderActivity.this.finish();
//			}
//		};
//
//	}
	
	
}
