package com.people.lyy.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.people.lyy.R;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.zxing.CameraManager;
import com.people.lyy.zxing.CaptureActivityHandler;
import com.people.lyy.zxing.InactivityTimer;
import com.people.lyy.zxing.LightControl;
import com.people.lyy.zxing.ViewfinderView;
import com.people.network.LKAsyncHttpResponseHandler;
import com.people.network.LKHttpRequest;
import com.people.network.LKHttpRequestQueue;
import com.people.network.LKHttpRequestQueueDone;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button btn_light_control, btn_back;
	private boolean isShow = false;

	private ProgressBar pg;
	private ImageView iv_pg_bg_grey;
	private String resultString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_diy);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		btn_light_control = (Button) this.findViewById(R.id.btn_light_control);
		btn_back = (Button) this.findViewById(R.id.btn_back);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});
		btn_light_control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LightControl mLightControl = new LightControl();

				if (isShow) {
					isShow = false;
					btn_light_control
							.setBackgroundResource(R.drawable.torch_off);
					mLightControl.turnOff();
				} else {
					isShow = true;
					btn_light_control
							.setBackgroundResource(R.drawable.torch_on);
					mLightControl.turnOn();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
		} else {
			if (pg != null && pg.isShown()) {
				pg.setVisibility(View.GONE);
				iv_pg_bg_grey.setVisibility(View.VISIBLE);
			}

			// 把二维码信息上传服务器
			upLoading();

		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	private void upLoading() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("token", resultString);
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

					Intent resultIntent = new Intent(CaptureActivity.this,
							SuccessActivity.class);
					resultIntent.putExtra("result", resultMap);
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

					Intent resultIntent = new Intent(CaptureActivity.this,
							DefeatedActivity.class);
					resultIntent.putExtra("result", msg);
					startActivity(resultIntent);
				}

			}
		};

	}
}