package com.xbd.kuk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xbd.kuk.bean.FriendMessage;
import com.xbd.kuk.util.Constants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessageHandler extends Handler {

	public static HashMap<String, ArrayList<FriendMessage>>  unReadMessageMap = new  HashMap<String, ArrayList<FriendMessage>>();
	public static ArrayList<FriendMessage> unReadList = new ArrayList<FriendMessage>();
	public static int unReadMessageNum = 0; //未读消息数量
	
	private MessageHelperCallBack mCallBack;
	private MqttClientHelper mqttClient;
	private MessageHandler(){
		                          
	}
	
    private static class SingletonHolder{
    	private static MessageHandler mHandler = new MessageHandler();
    }
	
    public static MessageHandler getInstance(){
    	return SingletonHolder.mHandler;
    }
    
	@Override
	public void handleMessage(Message msg) {
		if( mCallBack != null ){
			mCallBack.getNewMessage(msg); //分发给聊天页面自行处理
		}
		else{
			switch (msg.what) {
			case Constants.NEW_MESSAGE://接到1000002，服务器端有未读消息，去获取---等老马解决
			if( mqttClient != null)
				mqttClient.getNewMessge(MqttClientHelper.userSubNumber);
				break;
			case Constants.DEAL_NEW_MESSAGE: //未读消息处理，存入list
				mqttClient.new ParseMessage(MessageHandler.getInstance()).execute(new String[]{(String)msg.obj,Constants.FROM_MESSAEHANDLER});
				break;
			case Constants.DEAL_TOKEN_LOCKED:
				break;
			default:
				break;
			}
			
		}
		
		super.handleMessage(msg);
	}

	public void setMessageCallBack(MessageHelperCallBack mCall) {
		this.mCallBack = mCall;
	}

	public void setMqttClient(MqttClientHelper mChatClient) {
		this.mqttClient = mChatClient;
	}

	
	
	
}
