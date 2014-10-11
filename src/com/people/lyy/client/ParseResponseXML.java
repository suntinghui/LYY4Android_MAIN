package com.people.lyy.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.jababean.AccountInfo;

import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class ParseResponseXML {

	public static Object parseXML(int reqType, String responseStr) {
		Log.e("response:", responseStr);

		try {
			switch (reqType) {
			case TransferRequestTag.SignUp:
				return parseResponse(responseStr);

			case TransferRequestTag.Login: // 登录
				return parseResponse(responseStr);

			case TransferRequestTag.Accounts: // 离线消费
				return accounts(responseStr);

			case TransferRequestTag.getAccountStr:
				return responseStr;

			case TransferRequestTag.Generate: // 在线消费
				return parseResponse(responseStr);

			case TransferRequestTag.OnlineShop: // 在线消费
				return onLineShop(responseStr);

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

	public static List<AccountInfo> accounts(String str) {
		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();
		editor.putString(Constants.kACCOUNTLIST, str);
		editor.commit();

		List<AccountInfo> accountList = new ArrayList<AccountInfo>();
		try {
			String[] sss = str.split("#");
			String[] ss = sss[0].trim().split(";");
			if (ss.length == 0)
				return accountList;

			for (String temp : ss) {
				String[] s = temp.split(":");
				AccountInfo ban2 = new AccountInfo(s[0], s[1]);
				accountList.add(ban2);
			}

			return accountList;
		} catch (Exception e) {
			e.printStackTrace();

			return accountList;
		}
	}

	private static HashMap<String, String> onLineShop(String str) {
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

	// private static List<String> onLineShop2(String str) {
	// String[] ss = str.split("=");
	// List<String> list = new ArrayList<String>();
	// list.add(ss[1].substring(0, 8));
	// list.add(ss[1].substring(8, 21));
	// list.add(ss[1].substring(21, 28));
	// return list;
	// }

}
