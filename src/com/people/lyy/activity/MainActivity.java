package com.people.lyy.activity;

import java.util.Hashtable;
import java.util.Iterator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.people.lyy.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity implements OnClickListener {
	private LinearLayout lay_consume, lay_consume2, lay_binding,
			lay_gesture = null;
	private ImageView iv_consume = null;
	private boolean isShow = false;
	public static final int FROM_SETTINGACTIVITY = 1;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initview();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lay_consume:
			createImage();
			lay_consume2.setVisibility(0);
			isShow = true;
			break;

		case R.id.lay_binding:
			Intent intent0 = new Intent(BaseActivity.getTopActivity(),
					BindActivity.class);
			startActivity(intent0);
			break;

		case R.id.lay_gesture:
			Intent intent1 = new Intent(MainActivity.this,
					LockScreenSettingActivity.class);
			MainActivity.this.startActivity(intent1);
			break;

		default:
			break;
		}

	}

	public void initview() {
		lay_consume = (LinearLayout) findViewById(R.id.lay_consume);
		lay_consume.setOnClickListener(this);
		lay_binding = (LinearLayout) findViewById(R.id.lay_binding);
		lay_binding.setOnClickListener(this);
		lay_gesture = (LinearLayout) findViewById(R.id.lay_gesture);
		lay_gesture.setOnClickListener(this);
		lay_consume2 = (LinearLayout) findViewById(R.id.lay_consume2);
		iv_consume = (ImageView) findViewById(R.id.iv_consume);
	}

	// 创建二维码
	private void createImage() {
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			String text = "http://www.apk.anzhi.com/data2/apk/201409/22/com.people_00825300.apk";

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				if (isShow) {
					lay_consume2.setVisibility(8);
					isShow = false;
				} else {
					exitApp();
				}
			}
			return true;
		}
		return false;
	}

	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			showToast("再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			for (BaseActivity activity : BaseActivity.getAllActiveActivity()) {
				activity.finish();
			}
		}
	}
}