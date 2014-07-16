package com.xbd.kuk.model;

import android.os.Message;

/**
* @description 接收消息后回调，通知具体activity，发送消息过去
* @version 1.0
* @author wangpan
* @update 2013-11-1 下午04:45:52 
*/

public interface MessageHelperCallBack {

	public String  getNewMessage(Message content);
	
	
}
