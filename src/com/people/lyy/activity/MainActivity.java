package com.people.lyy.activity;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.people.lyy.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity implements OnClickListener {
	private Button btn_consume, btn_binding, btn_gesture = null;
	private LinearLayout lay_consume = null;
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
		case R.id.btn_consume:
			createImage();
			lay_consume.setVisibility(0);
			isShow = true;
			break;

		case R.id.btn_binding:
			showToast("binding");
			break;

		case R.id.btn_gesture:
			showToast("gesture");
			break;

		default:
			break;
		}

	}

	public void initview() {
		btn_consume = (Button) findViewById(R.id.btn_consume);
		btn_consume.setOnClickListener(this);
		btn_binding = (Button) findViewById(R.id.btn_binding);
		btn_binding.setOnClickListener(this);
		btn_gesture = (Button) findViewById(R.id.btn_gesture);
		btn_gesture.setOnClickListener(this);
		lay_consume = (LinearLayout) findViewById(R.id.lay_consume);
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
					lay_consume.setVisibility(8);
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
			finish();
		}
	}

}
