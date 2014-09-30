package com.people.lyy.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.util.Log;

public class ParseResponseXML {

	public static Object parseXML(int reqType, String responseStr) {
		Log.e("response:", responseStr);

		try {
			switch (reqType) {
			case TransferRequestTag.SignUp:
				return parseResponse(responseStr);

			case TransferRequestTag.Login: // 登录
				return parseResponse(responseStr);

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return null;
	}

	private static HashMap<String, String> parseResponse(String str) {
		String[] ss = str.split("&");
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < ss.length; i++) {
			String[] tt = ss[i].split("=");
			if (tt.length == 2) {
				map.put(tt[0].trim(), tt[1].trim());
			} else {
				map.put(tt[0].trim(), "");
			}
		}
		
		return map;
	}

}
