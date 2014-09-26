package com.people.network;

import android.util.Log;

import com.people.lyy.activity.BaseActivity;

public class LKHttpRequestQueueDone {
	
	private LKHttpRequestQueue queue;
	
	/***
	private Handler handler;
	
	public LKHttpRequestQueueDone() {
        // Set up a handler to post events back to the correct thread if possible
        if(Looper.myLooper() != null) {
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                	LKHttpRequestQueueDone.this.onComplete();
                }
            };
        }
    }
    
    ****/
	
	public void setRequestQueue(LKHttpRequestQueue queue){
		this.queue = queue;
	}

	/**
	 * 当队列内的请求全部执行 成功 后执行该方法
	 */
	public void onComplete(){
		Log.e("QueueDone Complete", "队列内请求全部执行 成功。。。");
	}
	
	
	/**
	 * 当队列内请求全部执行完成后执行该方法，有可能有失败的请求
	 */
	public void onFinish(){
		Log.e("QueueDone Finish", "队列内请求全部执行 完成。。。");
		
		LKHttpRequestQueue.queueList.remove(queue);
		
		try{
			// 不能关闭alert
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			//LKMagicToast.hideToast();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
