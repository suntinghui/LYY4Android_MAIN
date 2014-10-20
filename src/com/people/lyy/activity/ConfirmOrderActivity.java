package com.people.lyy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.ParseResponseXML;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.jababean.AccountInfo;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ConfirmOrderActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_ordercode, tv_shopcode, tv_time = null;
	private EditText et_psw = null;
	private Button btn_confirm = null;
	private List<AccountInfo> list_balance = null;
	private int total_cash = 0;
	private TextView tv_can_cost, tv_balance, tv_code = null;
	private MyAdapter adapter = null;
	private ListView lv_balance = null;
	private String token = null;
	private boolean toast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmorder);
		getAccounts();
		initView();
		// initData();
	}

	private void initView() {
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setText("交易日期:" + Constants.resultString.substring(0, 8));
		tv_shopcode = (TextView) findViewById(R.id.tv_shopcode);
		tv_shopcode.setText("商户：" + Constants.resultString.substring(8, 21));
		tv_ordercode = (TextView) findViewById(R.id.tv_ordercode);
		tv_ordercode.setText("订单号：" + Constants.resultString.substring(21, 28));
		// et_psw = (EditText) findViewById(R.id.et_psw);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		btn_confirm.setClickable(false);
		btn_confirm.setFocusable(false);

		tv_can_cost = (TextView) findViewById(R.id.tv_can_cost2);
		list_balance = new ArrayList<AccountInfo>();
		adapter = new MyAdapter(ConfirmOrderActivity.this);
		tv_balance = (TextView) findViewById(R.id.tv_balance2);
		lv_balance = (ListView) findViewById(R.id.lv_balance2);
		lv_balance.setAdapter(adapter);
		lv_balance.setOnItemClickListener(mLeftListOnItemClick);

	}

	public void initData() {
		String tempStr = ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constants.kACCOUNTLIST, "");
		list_balance = ParseResponseXML.accounts(tempStr);

		adapter.notifyDataSetChanged();
		for (int i = 0; i < list_balance.size(); i++) {
			total_cash = total_cash
					+ Integer.parseInt(list_balance.get(i).getCan_cost());
		}

		tv_balance.setText(total_cash + "元");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			pswDialog();
			break;

		default:
			break;
		}
	}

	protected void onNewIntent(Intent i) {
		token = i.getStringExtra("token");

		if (token.charAt(0) == '[') {
			if (toast) {
				showToast("非授权手机");
				hideDialog(PROGRESS_DIALOG);
				toast = false;
			}
		} else {
			upLoading(token);
		}
	}

	private void upLoading(String token) {

		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("trade", Constants.resultString);
		tempMap.put("token", token);

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
					Constants.resultCode = resultMap.get("card");
					Constants.resultBalance = resultMap.get("money");
					Intent resultIntent = new Intent(ConfirmOrderActivity.this,
							SuccessActivity.class);
					startActivityForResult(resultIntent, 100);

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
					startActivityForResult(resultIntent, 101);
				}

			}
		};

	}

	private void getAccounts() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("username", ApplicationEnvironment.getInstance()
				.getPreferences().getString(Constants.kUSERNAME, ""));
		tempMap.put("password", ApplicationEnvironment.getInstance()
				.getPreferences().getString(Constants.kPASSWORD, ""));

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Accounts,
				tempMap, getAccountsHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
				"正在加载数据请稍候。。。", new LKHttpRequestQueueDone() {
					@Override
					public void onComplete() {
						super.onComplete();
						BaseActivity.getTopActivity().hideDialog(
								ADPROGRESS_DIALOG);
					}

				});

	}

	public LKAsyncHttpResponseHandler getAccountsHandler() {

		return new LKAsyncHttpResponseHandler() {
			@Override
			public void successAction(Object obj) {

				list_balance = (List<AccountInfo>) obj;

				adapter.notifyDataSetChanged();

				for (int i = 0; i < list_balance.size(); i++) {
					total_cash = Integer.parseInt(total_cash
							+ list_balance.get(i).getCan_cost());
				}
				tv_balance.setText(total_cash + "元");
			}
		};

	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list_balance.size();
		}

		@Override
		public Object getItem(int position) {
			return list_balance.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_balance, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.tv_cardcode = (TextView) convertView
						.findViewById(R.id.tv_cardcode);
				holder.tv_cardbalance = (TextView) convertView
						.findViewById(R.id.tv_cardbalance);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String cardCode = list_balance.get(position).getBalance();
			holder.tv_cardcode.setText("(尾号)"
					+ cardCode.substring(cardCode.length() - 6,
							cardCode.length()));
			holder.tv_cardbalance.setText(list_balance.get(position)
					.getCan_cost());

			if (position == selectItem) {
				holder.imageView.setBackgroundResource(R.drawable.remeberpwd_s);
			} else {
				holder.imageView.setBackgroundResource(R.drawable.remeberpwd_n);
			}

			return convertView;
		}

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		public int getSelectItem() {
			return selectItem;
		}

		private int selectItem = -1;
	}

	static class ViewHolder {
		ImageView imageView;
		TextView tv_cardcode, tv_cardbalance;
	}

	AdapterView.OnItemClickListener mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Constants.GENTOKEN_ONLINE = true;

			adapter.setSelectItem(arg2);
			adapter.notifyDataSetChanged();
			btn_confirm.setClickable(true);
			btn_confirm.setFocusable(true);
			btn_confirm.setBackgroundResource(R.drawable.btn);
			tv_can_cost.setText(list_balance.get(arg2).getCan_cost() + "元");

		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			finish();
		}
	};

	public void pswDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		View view2 = LayoutInflater.from(ConfirmOrderActivity.this).inflate(
				R.layout.dialog_psw, null);

		final EditText editText_pwd = (EditText) view2
				.findViewById(R.id.password);
		dialog.setView(view2);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = editText_pwd.getText().toString().trim();
				if (password.equals(ApplicationEnvironment.getInstance()
						.getPreferences().getString(Constants.kPASSWORD, ""))) {
					Constants.GENTOKEN_ONLINE = false;
					Constants.SHOP_ONLINE = true;
					String selectedAccountNo = list_balance.get(
							((MyAdapter) lv_balance.getAdapter())
									.getSelectItem()).getBalance();
					String tempStr = ApplicationEnvironment.getInstance()
							.getPreferences()
							.getString(Constants.kUSERNAME, "")
							+ ":"
							+ selectedAccountNo
							+ ":"
							+ ApplicationEnvironment.getInstance()
									.getPreferences()
									.getString(Constants.kPASSWORD, "");

					Intent serviceIntent = new Intent(
							"com.people.sotp.lyyservice");
					serviceIntent.putExtra("SOTP", "genTOKEN");
					serviceIntent.putExtra("key", tempStr);

					startService(serviceIntent);
				} else {
					showToast("密码错误请重新输入");
				}
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}

		});
		dialog.create();
		dialog.show();

	}
}
