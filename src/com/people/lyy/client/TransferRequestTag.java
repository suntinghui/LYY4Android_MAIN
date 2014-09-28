package com.people.lyy.client;

import android.annotation.SuppressLint;
import java.util.HashMap;


@SuppressLint("UseSparseArrays")
public class TransferRequestTag {

	public static final int Login = 1;// 登录

	public static final int UpdateVersion = 0;

	private static HashMap<Integer, String> requestTagMap = null;

	public static HashMap<Integer, String> getRequestTagMap() {
		if (null == requestTagMap) {
			requestTagMap = new HashMap<Integer, String>();

			requestTagMap.put(Login, Constants.ip + "199002.tranm");
			requestTagMap
					.put(UpdateVersion,
							"http://"
									+ Constants.ipCash
									+ "/zfb/mpos/transProcess.do?operationId=getVersion&type=2");

		}

		return requestTagMap;
	}

}
