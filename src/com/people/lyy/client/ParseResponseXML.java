package com.people.lyy.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.people.lyy.jababean.AccountInfo;

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

			case TransferRequestTag.Accounts: 
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

	private static List<AccountInfo> parseResponse2(String str) {
		List<AccountInfo> accountList = new ArrayList<AccountInfo>();
		try{
			String[] ss = str.split(";");
			if (ss.length==0) 
				return accountList;
			
			for (String temp : ss){
				String[] s = temp.split(":");
				AccountInfo ban2 = new AccountInfo(s[0], s[1]);
				accountList.add(ban2);
			}
			
			return accountList;
		} catch(Exception e){
			e.printStackTrace();
			
			return accountList;
		}
	}

}
