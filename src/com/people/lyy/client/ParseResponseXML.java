package com.people.lyy.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

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
			case TransferRequestTag.Login: // 登录
				return login();

			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();

		} catch (IOException e) {
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

	// 登录
	private static HashMap<String, Object> login() throws XmlPullParserException, IOException {
		HashMap<String, Object> respMap = null;

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("EPOSPROTOCOL".equalsIgnoreCase(parser.getName())) {
					respMap = new HashMap<String, Object>();
				} else if ("PHONENUMBER".equalsIgnoreCase(parser.getName())) {
					respMap.put("PHONENUMBER", parser.nextText());
				} else if ("RSPCOD".equalsIgnoreCase(parser.getName())) {
					respMap.put("RSPCOD", parser.nextText());
				} else if ("APPTOKEN".equalsIgnoreCase(parser.getName())) {
					respMap.put("APPTOKEN", parser.nextText());
				} else if ("RSPMSG".equalsIgnoreCase(parser.getName())) {
					respMap.put("RSPMSG", parser.nextText());
				} else if ("PACKAGEMAC".equalsIgnoreCase(parser.getName())) {
					respMap.put("PACKAGEMAC", parser.nextText());
				}
				break;
			}

			eventType = parser.next();
		}

		return respMap;
	}

	
}
