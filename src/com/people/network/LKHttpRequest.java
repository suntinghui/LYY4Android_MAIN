package com.people.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.people.lyy.client.ApplicationEnvironment;
import com.people.lyy.client.TransferRequestTag;

public class LKHttpRequest {

	private int tag;
	private int methodTag;
	private HashMap<String, Object> requestDataMap;
	private LKAsyncHttpResponseHandler responseHandler;
	private AsyncHttpClient client;
	private LKHttpRequestQueue queue;

	public LKHttpRequest(int methodTag, HashMap<String, Object> requestMap, LKAsyncHttpResponseHandler handler) {
		this.methodTag = methodTag;
		this.requestDataMap = requestMap;
		this.responseHandler = handler;
		client = new AsyncHttpClient();

		if (null != this.responseHandler) {
			this.responseHandler.setRequest(this);
		}
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getMethodTag() {
		return methodTag;
	}

	public LKHttpRequestQueue getRequestQueue() {
		return this.queue;
	}

	public void setRequestQueue(LKHttpRequestQueue queue) {
		this.queue = queue;
	}

	public HashMap<String, Object> getRequestDataMap() {
		return requestDataMap;
	}

	public LKAsyncHttpResponseHandler getResponseHandler() {
		return responseHandler;
	}

	public AsyncHttpClient getClient() {
		return client;
	}

	/****************************************/

	public void post() {
		this.client.post(ApplicationEnvironment.getInstance().getApplication(), TransferRequestTag.getRequestTagMap().get(this.getMethodTag()), this.getHttpEntity(this), null, this.responseHandler);
	}

	private HttpEntity getHttpEntity(LKHttpRequest request) {

		StringBuffer bodySB = new StringBuffer();
		bodySB.append(this.param2String(request.getRequestDataMap()));

		request.getClient().addHeader("Content-Length", bodySB.length() + "");

		Log.i("request body:", bodySB.toString());

		HttpEntity entity = null;
		try {
			entity = new StringEntity(bodySB.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	private String param2String(HashMap<String, Object> paramMap) {
		StringBuffer sb = new StringBuffer();

		for (String key : paramMap.keySet()) {
			Object obj = paramMap.get(key);
			sb.append(key).append("=").append(obj).append("&");
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
}
