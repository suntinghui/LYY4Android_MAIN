package com.people.lyy.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.util.Log;

public class ParseResponseXML {

	private static InputStream inStream = null;

	public static Object parseXML(int reqType, String responseStr) {
		Log.e("response:", responseStr);

		try {
			inStream = new ByteArrayInputStream(responseStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			switch (reqType) {
			case TransferRequestTag.SignUp:
				return signUp();

			case TransferRequestTag.Login: // 登录
				return login();

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (null != inStream)
					inStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// 注册
	private static HashMap<String, String> signUp() {
		HashMap<String, String> respMap = null;

		return respMap;
	}

	// 登录
	private static HashMap<String, String> login() {
		HashMap<String, String> respMap = null;

		return respMap;
	}

}
