package com.people.network;

import java.util.ArrayList;

import com.people.lyy.activity.BaseActivity;
import com.people.lyy.client.ApplicationEnvironment;

public class LKHttpRequestQueue {

	private ArrayList<LKHttpRequest> requestList = null;

	private int completedTag = 0;
	private int finishedTag = 0;
	private LKHttpRequestQueueDone queueDone;

	public static ArrayList<LKHttpRequestQueue> queueList = new ArrayList<LKHttpRequestQueue>();

	public LKHttpRequestQueue() {
		requestList = new ArrayList<LKHttpRequest>();

		queueList.add(this);
	}

	public LKHttpRequestQueue addHttpRequest(LKHttpRequest... httpRequests) {
		for (int i = 0; i < httpRequests.length; i++) {
			LKHttpRequest request = httpRequests[i];
			request.setTag((int) Math.pow(2, requestList.size()));
			request.setRequestQueue(this);
			requestList.add(request);
		}

		return this;
	}

	public void executeQueue(String prompt, LKHttpRequestQueueDone queueDone) {
		if (!ApplicationEnvironment.getInstance().checkNetworkAvailable()) {
			BaseActivity.getTopActivity().showToast("网络连接不可用，请稍候重试");
			return;
		}
		if (null != prompt) {
			BaseActivity.getTopActivity().showDialog(
					BaseActivity.ADPROGRESS_DIALOG, prompt);
		}

		this.queueDone = queueDone;
		this.queueDone.setRequestQueue(this);

		this.completedTag = 0;
		this.finishedTag = 0;

		for (LKHttpRequest request : requestList) {
			request.post();
		}
	}

	public void updateCompletedTag(int tag) {
		synchronized (this) {
			this.completedTag += tag;

			if (completedTag == (int) Math.pow(2, this.requestList.size()) - 1) {
				// 在updataFinishedTag清空
				// requestList.clear();
				this.queueDone.onComplete();
			}
		}
	}

	public void updataFinishedTag(int tag) {
		synchronized (this) {
			this.finishedTag += tag;

			if (this.finishedTag == (int) Math.pow(2, this.requestList.size()) - 1) {
				requestList.clear();
				this.queueDone.onFinish();
			}
		}
	}

	public void cancel() {
		for (LKHttpRequest request : requestList) {
			request.getClient()
					.cancelRequests(
							ApplicationEnvironment.getInstance()
									.getApplication(), true);
		}

		requestList.clear();
	}

}
