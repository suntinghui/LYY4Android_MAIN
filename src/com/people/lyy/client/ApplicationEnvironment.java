package com.people.lyy.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.people.lyy.activity.BaseActivity;

public class ApplicationEnvironment {

	public static final String POS_PEOPLE = "LYY_SCAN";

	private static ApplicationEnvironment appEnv = null;
	private Application application = null;
	private SharedPreferences preferences = null;

	// 屏幕高度和宽度
	public int screenWidth = 0;
	public int screenHeight = 0;

	public static ApplicationEnvironment getInstance() {
		if (null == appEnv) {
			appEnv = new ApplicationEnvironment();

		}

		return appEnv;
	}

	public Application getApplication() {
		if (null == this.application) {
			this.application = BaseActivity.getTopActivity().getApplication();
		}

		return this.application;
	}

	public DisplayMetrics getPixels() {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) getApplication().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	public SharedPreferences getPreferences() {
		if (null == preferences)
			preferences = this.getApplication().getSharedPreferences(
					ApplicationEnvironment.POS_PEOPLE, Context.MODE_PRIVATE);

		return preferences;
	}

	public SharedPreferences getPreferences(Context context) {
		if (null == preferences)
			preferences = context.getSharedPreferences(
					ApplicationEnvironment.POS_PEOPLE, Context.MODE_PRIVATE);

		return preferences;
	}

	public boolean checkNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) this
				.getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null)
			return false;

		NetworkInfo netinfo = manager.getActiveNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		if (netinfo.isConnected()) {
			return true;
		}
		return false;
	}

	public void ForceLogout() {
		try {

			cleanUp();

			appEnv = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void cleanUp() {
		// 停止超时服务
		this.getApplication().stopService(new Intent("com.dhc.timeoutService"));

	}

	public boolean checkNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null)
			return false;

		NetworkInfo netinfo = manager.getActiveNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		if (netinfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	
}
