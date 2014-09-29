package com.people.lyy.client;

import android.annotation.SuppressLint;
import java.util.HashMap;

@SuppressLint("UseSparseArrays")
public class TransferRequestTag {

	public static final int SignUp	= 0; // 注册
	public static final int Login 	= 1;// 登录

	private static HashMap<Integer, String> requestTagMap = null;

	public static HashMap<Integer, String> getRequestTagMap() {
		if (null == requestTagMap) {
			requestTagMap = new HashMap<Integer, String>();

			requestTagMap.put(SignUp, Constants.IP + "/sslvpn/index.php/login/register");
			requestTagMap.put(Login, Constants.IP + "/sslvpn/index.php/login/login_verify");

		}

		return requestTagMap;
	}

}
