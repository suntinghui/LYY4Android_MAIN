package com.people.network;

import org.apache.http.client.HttpResponseException;

import android.content.DialogInterface;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.people.lyy.activity.BaseActivity;
import com.people.lyy.client.ParseResponseXML;
import com.people.lyy.client.TransferRequestTag;
import com.people.lyy.view.LKAlertDialog;

public abstract class LKAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
	
	private LKHttpRequest request;
	
	public void setRequest(LKHttpRequest request){
		this.request = request;
	}
	
	public abstract void successAction(Object obj);
	
	public void failureAction(Throwable error, String content){
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void onSuccess(String content) {
		Log.e("RESPONSE", content);
		
		super.onSuccess(content);
		
		Object obj = ParseResponseXML.parseXML(request.getMethodTag(), content);
		
		Log.e("success", "try to do success action..." + TransferRequestTag.getRequestTagMap().get(request.getMethodTag()));
		
		successAction(obj);
		
		try{
			// 如果不通过队列单独发起一人LKHttpRequest请求则会导致异常。
			if (null != this.request.getRequestQueue()){
				// 当然也不需要去通知队列执行完成。
				this.request.getRequestQueue().updateCompletedTag(this.request.getTag());
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onFailure(final Throwable error, final String content) {
		super.onFailure(error, content);
		
		try{
			HttpResponseException exception = (HttpResponseException) error;
			Log.e("Status Code","" + exception.getStatusCode());
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			Log.e("error content:", content);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			Log.e("failure:", error.getCause().toString());
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			Log.e("failure message:", error.getCause().getMessage());
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		// 当有请求失败的时候，终止所有的请求
		//LKHttpRequestQueue.sharedClient().cancelRequests(ApplicationEnvironment.getInstance().getApplication(), true);
		//LKHttpRequestQueue.sharedClient().getHttpClient().getConnectionManager().shutdown();
		
		try{
			if (null != this.request.getRequestQueue()){
				this.request.getRequestQueue().cancel();
			} 
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		BaseActivity.getTopActivity().hideDialog(BaseActivity.ALL_DIALOG);
		
		
		LKAlertDialog dialog = new LKAlertDialog(BaseActivity.getTopActivity());
		dialog.setTitle("提示");
		
		try{
			dialog.setMessage(getErrorMsg(error.toString()));
		} catch(Exception e){
			dialog.setMessage(getErrorMsg(null));
		}
		
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				
				failureAction(error, content);
			}
		});
		dialog.create().show();
		
		Log.e("error:", error.toString() );
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
		
		try{
			if (null != this.request.getRequestQueue()){
				this.request.getRequestQueue().updataFinishedTag(this.request.getTag());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private String getErrorMsg(String content){
		if (null == content)
			return "服务器异常，请与管理员联系或稍候再试。";
		
		//  org.apache.http.conn.ConnectTimeoutException: Connect to /124.205.53.178:9596 timed out
		if (content.contains("ConnectTimeoutException") || content.contains("SocketTimeoutException") ) { 
			return "连接服务器超时，请检查您的网络情况或稍候再试。";
		} else if(content.contains("HttpHostConnectException") || content.contains("ConnectException")){
			return "连接服务器超时，请检查您的网络情况或稍候再试。";
		} else if(content.contains("Bad Request")){ // org.apache.http.client.HttpResponseException: Bad Request
			return "服务器地址发生更新，请与管理员联系或稍候再试。";
		} else if (content.contains("time out")){ // socket time out
			return "连接服务器超时，请重试。";
		} else if (content.contains("can't resolve host") || content.contains("400 Bad Request")) {
			return "连接服务器出错，请确定您的连接服务器地址是否正确。";
		} else if (content.contains("UnknownHostException")){ // java.net.UnknownHostException: Unable to resolve host "oagd.crbcint.com": No address associated with hostname
			return "网络异常，无法连接服务器。";
		}
		
		//return "处理请求出错[" + content +"]";
		return "服务器响应异常,请重新操作。";
	}

}
