package com.people.lyy.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.jababean.Balance;

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

			case TransferRequestTag.Accounts: // 登录
				return parseResponse2(responseStr);

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

	private static List<Balance> parseResponse2(String str) {
		List<Balance> balance = new ArrayList<Balance>();
		if (str.contains(";")) {
			String[] ss = str.split(";");
			for(int i = 0 ; i < ss.length ; i++){
				String [] s = ss[i].split(":");
				Balance ban1 = new Balance(s[0],s[1]);
				balance.add(ban1);
			}
		} else {
			String[] s = str.split(":");
			for (int j = 0; j < balance.size(); j++) {
				Balance ban2 = new Balance(s[0], s[1]);
				balance.add(ban2);
			}
		}
		return balance;
	}

}
