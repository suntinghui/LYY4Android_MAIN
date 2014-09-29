package com.people.lyy.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.Constants;
import com.people.lyy.view.LKAlertDialog;



import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class TimeoutService extends Service {
	
	private static final int EVENT_LOCK_WINDOW 	= 0x100;
	
	private static final int CHECK_FREQUENCY 	= 20000; // 每隔20S检测一次是否超时
	
	private static long MAX_TIMEOUT 	   	    = Constants.OVERTIME * 60 * 1000;
	
	public static long	LastSystemTimeMillis   	= System.currentTimeMillis();
	
	private Handler mHandler 		= null;
	private Timer	mTimer 			= null;
	private TimerTask mTask 		= null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MAX_TIMEOUT = Constants.OVERTIME * 60 * 1000;
		
		if (null != mTimer && null != mTask){
			mTask.cancel();
		}
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				lockSystem();
			}
		};
		
		mTimer = new Timer(true);
		mTask = new LockTimerTask();
		
		mTimer.schedule(mTask, CHECK_FREQUENCY, CHECK_FREQUENCY); // 每分钟检测一次是否锁屏
		
		// 使用START_NOT_STICKY返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务。
		return START_STICKY;
	}
	
	private void lockSystem(){
		if (null != mTimer && null != mTask){
			mTask.cancel();
		}
		
		this.stopSelf();
		
		
		Boolean isOpen = ApplicationEnvironment.getInstance().getPreferences(this).getBoolean(Constants.kGESTRUECLOSE, false);
		if(isOpen){
			Intent intent = new Intent("com.people.LockScreenActivity");
			intent.putExtra("TIMEOUT", true);
			BaseActivity.getTopActivity().startActivityForResult(intent, 10);
		}else{
			
			LKAlertDialog dialog = new LKAlertDialog(BaseActivity.getTopActivity());
			dialog.setTitle("提示");
			dialog.setMessage("系统超时，请重新登录");
			dialog.setCancelable(false);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					while (!(BaseActivity.getTopActivity() instanceof LoginActivity)){
						BaseActivity.getTopActivity().finish();
					}
				}
			});
			
			dialog.create().show();
		}
		
		
		
	}
	
	class LockTimerTask extends TimerTask{
		@Override
		public void run() {
			if (System.currentTimeMillis() - LastSystemTimeMillis > MAX_TIMEOUT){
				Message msg = new Message();
				msg.what = EVENT_LOCK_WINDOW;
				msg.setTarget(mHandler);
				msg.sendToTarget();
			}
		}
	}

}
