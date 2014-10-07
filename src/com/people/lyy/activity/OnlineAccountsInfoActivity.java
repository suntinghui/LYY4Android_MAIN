package com.people.lyy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.people.lyy.R;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.jababean.AccountInfo;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//10表示用户不存在 11表示二维码超时

public class OnlineAccountsInfoActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout lay_consume2 = null;
	private ImageView iv_consume = null;
	private boolean isShow = false;
	private Button btn_back, btn_confirm = null;
	private ListView lv_balance = null;
	private List<AccountInfo> list_balance = null;
	private MyAdapter adapter = null;
	private boolean isClick = true;
	private int item_index = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountsinfo);

		getAccounts();

		initview();

	}

	protected void onNewIntent(Intent i) {
		createImage(i.getStringExtra("token"));
		isShow = true;
		lay_consume2.setVisibility(View.VISIBLE);

		this.hideDialog(BaseActivity.PROGRESS_DIALOG);
	}

	public void initview() {

		lay_consume2 = (LinearLayout) findViewById(R.id.lay_consume2);
		iv_consume = (ImageView) findViewById(R.id.iv_consume);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		list_balance = new ArrayList<AccountInfo>();

		lv_balance = (ListView) findViewById(R.id.lv_balance);
		adapter = new MyAdapter(OnlineAccountsInfoActivity.this);
		lv_balance.setAdapter(adapter);
		lv_balance.setOnItemClickListener(mLeftListOnItemClick);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				if (isShow) {
					lay_consume2.setVisibility(View.GONE);
					isShow = false;
				} else {
					finish();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.btn_confirm:
			this.showDialog(BaseActivity.PROGRESS_DIALOG, "正在加密请稍候");

			String selectedAccountNo = list_balance.get(
					((MyAdapter) lv_balance.getAdapter()).getSelectItem())
					.getBalance();
			String tempStr = ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constants.kUSERNAME, "")
					+ ":" + selectedAccountNo;

			Intent serviceIntent = new Intent("com.people.sotp.lyyservice");
			serviceIntent.putExtra("SOTP", "genTOKEN");
			serviceIntent.putExtra("key", tempStr);
			startService(serviceIntent);

			break;

		default:
			break;
		}

	}

	// 创建二维码
	private void createImage(String text) {
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, 350,
					350);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, 350, 350, hints);
			int[] pixels = new int[350 * 350];
			for (int y = 0; y < 350; y++) {
				for (int x = 0; x < 350; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * 350 + x] = 0xff000000;
					} else {
						pixels[y * 350 + x] = 0xffffffff;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(350, 350,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, 350, 0, 0, 350, 350);
			iv_consume.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	AdapterView.OnItemClickListener mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			adapter.setSelectItem(arg2);
			adapter.notifyDataSetChanged();
			item_index = arg2;
		}

	};

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
			holder.tv_cardcode.setText(list_balance.get(position).getBalance());
			holder.tv_cardbalance.setText(list_balance.get(position)
					.getCan_cost());

			if (selectItem == item_index) {
				holder.imageView.setBackgroundResource(R.drawable.remeberpwd_n);
				btn_confirm.setVisibility(View.GONE);
			} else if (position == selectItem) {
				holder.imageView.setBackgroundResource(R.drawable.remeberpwd_s);
				btn_confirm.setVisibility(View.VISIBLE);
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

					}

				});

	}

	public LKAsyncHttpResponseHandler getAccountsHandler() {

		return new LKAsyncHttpResponseHandler() {
			@Override
			public void successAction(Object obj) {
				list_balance = (List<AccountInfo>) obj;

			}
		};

	}
}
