package com.people.lyy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import android.widget.Toast;

//10表示用户不存在 11表示二维码超时
public class OnlineAccountsInfoActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout lay_consume2, lay_bigone, lay_bigtwo = null;
	private ImageView iv_consume, iv_consume2, iv_bigone, iv_bigtwo = null;
	private Button btn_back, btn_confirm = null;
	private ListView lv_balance = null;
	private List<AccountInfo> list_balance = null;
	private MyAdapter adapter = null;
	private TextView tv_can_cost, tv_balance, tv_code, tv_time = null;
	private int total_cash = 0;
	private boolean codeShow = false;

	private long count = 60;
	private Timer timer = null;
	private TimerTask task = null;

	public static boolean isShow = false;
	public static String code = null;
	public boolean toast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onlineaccountsinfo);

		getAccounts();

		initView();

	}

	protected void onNewIntent(Intent i) {

		String[] s = i.getStringExtra("token").split("#");
		Log.i("return", s[0]);
		if (s[0].charAt(0) == '[') {
			if (toast) {
				showToast("非授权手机");
				hideDialog(PROGRESS_DIALOG);
				toast = false;
			}
		} else {
			try {
				code = s[0];
				iv_consume.setImageBitmap(createOneDCode(s[0]));
				// tv_code.setText(code);
				tv_code.setText(code.substring(0, 11) + "    "
						+ code.substring(11, 19));
				iv_consume2.setImageBitmap(createTwoDCode(s[0]));

			} catch (WriterException e) {
				e.printStackTrace();
			}

			isShow = true;
			lay_consume2.setVisibility(View.VISIBLE);

			this.hideDialog(BaseActivity.PROGRESS_DIALOG);
		}
	}

	public void initView() {
		lay_bigone = (LinearLayout) findViewById(R.id.lay_bigone);
		iv_bigone = (ImageView) findViewById(R.id.iv_bigone);
		iv_bigone.setOnClickListener(this);
		lay_bigtwo = (LinearLayout) findViewById(R.id.lay_bigtwo);
		iv_bigtwo = (ImageView) findViewById(R.id.iv_bigtwo);
		iv_bigtwo.setOnClickListener(this);

		lay_consume2 = (LinearLayout) findViewById(R.id.lay_consume2);
		iv_consume = (ImageView) findViewById(R.id.iv_consume);
		iv_consume.setOnClickListener(this);
		iv_consume2 = (ImageView) findViewById(R.id.iv_consume2);
		iv_consume2.setOnClickListener(this);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		btn_confirm.setClickable(false);
		btn_confirm.setFocusable(false);
		btn_confirm.setBackgroundColor(Color.GRAY);

		list_balance = new ArrayList<AccountInfo>();

		lv_balance = (ListView) findViewById(R.id.lv_balance);
		adapter = new MyAdapter(OnlineAccountsInfoActivity.this);
		lv_balance.setAdapter(adapter);
		lv_balance.setOnItemClickListener(mLeftListOnItemClick);

		tv_can_cost = (TextView) findViewById(R.id.tv_can_cost);
		tv_balance = (TextView) findViewById(R.id.tv_balance);
		tv_code = (TextView) findViewById(R.id.tv_code);
		tv_time = (TextView) findViewById(R.id.tv_time);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				if (codeShow) {
					lay_bigone.setVisibility(View.GONE);
					lay_bigtwo.setVisibility(View.GONE);
					codeShow = false;
				} else if (isShow) {
					lay_consume2.setVisibility(View.GONE);
					count = 60;
					count = 60;
					timer.cancel();
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
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {

					runOnUiThread(new Runnable() { // UI thread
						@Override
						public void run() {
							count--;
							tv_time.setText("距离刷新还有" + count + "秒");
							if (count == 0) {
								setData();
								count = 60;
							}
						}
					});
				}
			};
			timer.schedule(task, 1000, 1000);

			Constants.SHOP_ONLINE = false;
			this.showDialog(BaseActivity.PROGRESS_DIALOG, "正在加密请稍候");
			setData();

			break;
		case R.id.iv_consume:
			lay_bigone.setVisibility(View.VISIBLE);
			codeShow = true;
			break;
		case R.id.iv_consume2:
			lay_bigtwo.setVisibility(View.VISIBLE);
			codeShow = true;
			break;
		case R.id.iv_bigone:
			lay_bigone.setVisibility(View.GONE);
			break;
		case R.id.iv_bigtwo:
			lay_bigtwo.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	private LKAsyncHttpResponseHandler getGenerateHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				BaseActivity.getTopActivity().hideDialog(ADPROGRESS_DIALOG);

				HashMap<String, String> map = (HashMap<String, String>) obj;
				int ret = Integer.parseInt(map.get("ret"));
				if (ret == 0) {
					try {
						tv_code.setText(map.get("token").substring(0, 11)
								+ "     " + map.get("token").substring(11, 19));
						createOneDCode(map.get("token"));
					} catch (WriterException e) {
						e.printStackTrace();
					}
					createTwoDCode(map.get("token"));
					Log.i("token", map.get("token"));
					isShow = true;
					lay_consume2.setVisibility(View.VISIBLE);

				} else if (ret == 10) {
					OnlineAccountsInfoActivity.this.showDialog(
							BaseActivity.MODAL_DIALOG, "用户不存在！");
				} else if (ret == 13) {
					OnlineAccountsInfoActivity.this.showDialog(
							BaseActivity.MODAL_DIALOG, "帐号不存在！");
				}

			}

		};
	}

	public void setData() {
		String selectedAccountNo = list_balance.get(
				((MyAdapter) lv_balance.getAdapter()).getSelectItem())
				.getBalance();
		String tempStr = ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constants.kUSERNAME, "")
				+ ":"
				+ selectedAccountNo
				+ ":"
				+ ApplicationEnvironment.getInstance().getPreferences()
						.getString(Constants.kPASSWORD, "")
				+ ":"
				+ Constants.IP.replace("http://", "");
		Intent serviceIntent = new Intent("com.people.sotp.lyyservice");
		serviceIntent.putExtra("SOTP", "genTOKEN");
		serviceIntent.putExtra("key", tempStr);
		startService(serviceIntent);
	}

	// 创建二维码
	private Bitmap createTwoDCode(String text) {
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			if (text == null || "".equals(text) || text.length() < 1) {
				return null;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, 450,
					450);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, 450, 450, hints);
			int[] pixels = new int[450 * 450];
			for (int y = 0; y < 450; y++) {
				for (int x = 0; x < 450; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * 450 + x] = 0xff000000;
					} else {
						pixels[y * 450 + x] = 0xffffffff;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(450, 450,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, 450, 0, 0, 450, 450);
			iv_bigtwo.setImageBitmap(bitmap);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
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

	public Bitmap createOneDCode(String content) throws WriterException {
		// 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.CODE_128, 800, 400);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		Matrix matrix2 = new Matrix();
		matrix2.postRotate(90);
		matrix2.setRotate(90);
		Bitmap matrixBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix2, true);
		iv_bigone.setImageBitmap(matrixBitmap);
		return bitmap;
	}
}
