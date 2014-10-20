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
import com.people.lyy.activity.ConfirmOrderActivity.MyAdapter;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AccountsInfoActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout lay_consume2, lay_bigtwo, lay_bigone = null;
	private ImageView iv_consume, iv_consume2, iv_bigone, iv_bigtwo = null;
	private boolean codeShow = false;
	private Button btn_back, btn_confirm = null;
	private ListView lv_balance = null;
	private List<AccountInfo> list_balance = null;
	private MyAdapter adapter = null;
	private TextView tv_can_cost, tv_balance, tv_code, tv_bigone,
			tv_time = null;
	private int total_cash = 0;
	private long count = 60;
	private Timer timer = null;
	private TimerTask task = null;
	public static boolean isShow = false;
	public static String code = null;
	public boolean toast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountsinfo);

		initView();

		initData();
	}

	protected void onNewIntent(Intent i) {
		String[] s = i.getStringExtra("token").split("#");
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

		lay_consume2 = (LinearLayout) findViewById(R.id.lay_consume2);
		iv_consume = (ImageView) findViewById(R.id.iv_consume);
		iv_consume.setOnClickListener(this);
		iv_consume2 = (ImageView) findViewById(R.id.iv_consume2);
		iv_consume2.setOnClickListener(this);
		lay_bigone = (LinearLayout) findViewById(R.id.lay_bigone);
		iv_bigone = (ImageView) findViewById(R.id.iv_bigone);
		iv_bigone.setOnClickListener(this);
		// tv_bigone = (TextView) findViewById(R.id.tv_bigone);
		lay_bigtwo = (LinearLayout) findViewById(R.id.lay_bigtwo);
		iv_bigtwo = (ImageView) findViewById(R.id.iv_bigtwo);
		iv_bigtwo.setOnClickListener(this);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		btn_confirm.setClickable(false);
		btn_confirm.setFocusable(false);
		btn_confirm.setBackgroundColor(Color.GRAY);

		list_balance = new ArrayList<AccountInfo>();

		lv_balance = (ListView) findViewById(R.id.lv_balance);
		adapter = new MyAdapter(AccountsInfoActivity.this);
		lv_balance.setAdapter(adapter);
		lv_balance.setOnItemClickListener(mLeftListOnItemClick);

		tv_can_cost = (TextView) findViewById(R.id.tv_can_cost);
		tv_balance = (TextView) findViewById(R.id.tv_balance);

		tv_code = (TextView) findViewById(R.id.tv_code);
		tv_time = (TextView) findViewById(R.id.tv_time2);

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

	public void resetData() {
		String selectedAccountNo = list_balance.get(
				((MyAdapter) lv_balance.getAdapter()).getSelectItem())
				.getBalance();
		String tempStr = ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constants.kUSERNAME, "")
				+ ":"
				+ selectedAccountNo
				+ ":"
				+ ApplicationEnvironment.getInstance().getPreferences()
						.getString(Constants.kPASSWORD, "");

		Intent serviceIntent = new Intent("com.people.sotp.lyyservice");
		serviceIntent.putExtra("SOTP", "genTOKEN");
		serviceIntent.putExtra("key", tempStr);
		Log.e("开启服务", "这里走了");
		startService(serviceIntent);
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
					isShow = false;
					count = 60;
					timer.cancel();
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
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			View view2 = LayoutInflater.from(AccountsInfoActivity.this)
					.inflate(R.layout.dialog_psw, null);

			final EditText editText_pwd = (EditText) view2
					.findViewById(R.id.password);
			dialog.setView(view2);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String password = editText_pwd.getText().toString()
									.trim();
							if (password.equals(ApplicationEnvironment
									.getInstance().getPreferences()
									.getString(Constants.kPASSWORD, ""))) {
								timer = new Timer();
								task = new TimerTask() {
									@Override
									public void run() {

										runOnUiThread(new Runnable() { // UI
											// thread
											@Override
											public void run() {
												count--;
												tv_time.setText("距离刷新还有"
														+ count + "秒");
												if (count == 0) {
													resetData();
													count = 60;
												}
											}
										});
									}
								};
								timer.schedule(task, 1000, 1000);

								Constants.GENTOKEN_ONLINE = false;
								Constants.SHOP_ONLINE = false;
								AccountsInfoActivity.this
										.showDialog(
												BaseActivity.PROGRESS_DIALOG,
												"正在加密请稍候");
								resetData();
							} else {
								showToast("密码错误请重新输入");
							}
						}
					});
			dialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}

					});
			dialog.create();
			dialog.show();

			break;
		case R.id.iv_consume:
			lay_bigone.setVisibility(View.VISIBLE);
			try {
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				matrix.setRotate(90);
				Bitmap matrixBitmap = Bitmap.createBitmap(createOneDCode(code),
						0, 0, createOneDCode(code).getWidth(),
						createOneDCode(code).getHeight(), matrix, true);
				iv_bigone.setImageBitmap(matrixBitmap);
				// 使textView竖排显示并赋值但是效果不好
				// tv_bigone.setText(code.substring(0, 10) + "     "
				// + code.substring(11, 19));
				// Animation anim = new RotateAnimation(0.0f, 90.0f,
				// Animation.RELATIVE_TO_SELF, 0.5f,
				// Animation.RELATIVE_TO_SELF, 0.5f);
				// anim.setDuration(1);
				// anim.setFillAfter(true);
				// tv_bigone.setAnimation(anim);

			} catch (WriterException e) {
				e.printStackTrace();
			}
			codeShow = true;
			break;
		case R.id.iv_bigone:
			lay_bigone.setVisibility(View.GONE);
			break;

		case R.id.iv_consume2:
			lay_bigtwo.setVisibility(View.VISIBLE);
			iv_bigtwo.setImageBitmap(createTwoDCode(code));
			codeShow = true;
			break;

		case R.id.iv_bigtwo:
			lay_bigtwo.setVisibility(View.GONE);
			break;

		default:
			break;
		}

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
			return bitmap;
			// iv_consume2.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	AdapterView.OnItemClickListener mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

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
			holder.tv_cardbalance.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
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
		return bitmap;
	}

}
