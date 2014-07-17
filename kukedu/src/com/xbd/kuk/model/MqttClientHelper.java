package com.xbd.kuk.model;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ibm.mqtt.MqttAdvancedCallback;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttNotConnectedException;
import com.ibm.mqtt.MqttPersistenceException;
import com.xbd.kuk.MainActivity;
import com.xbd.kuk.R;
import com.xbd.kuk.app.Chat2Activity;
import com.xbd.kuk.bean.FormFile;
import com.xbd.kuk.bean.FriendMessage;
import com.xbd.kuk.datastart.DataUtils;
import com.xbd.kuk.util.Configuration;
import com.xbd.kuk.util.Constants;
import com.xbd.kuk.util.JsonHelper;

public class MqttClientHelper extends Service implements MqttAdvancedCallback {

	// constants used to define MQTT connection status
	public enum MQTTConnectionStatus {
		INITIAL, // initial status
		CONNECTING, // attempting to connect
		CONNECTED, // connected
		NOTCONNECTED_WAITINGFORINTERNET, // can't connect because the phone
											// does not have Internet access
		NOTCONNECTED_USERDISCONNECT, // user has explicitly requested
										// disconnection
		NOTCONNECTED_DATADISABLED, // can't connect because the user
									// has disabled data access
		NOTCONNECTED_UNKNOWNREASON // failed to connect for some reason
	}

	// status of MQTT client connection
	private MQTTConnectionStatus connectionStatus = MQTTConnectionStatus.INITIAL;
	// constant used internally to schedule the next ping event
	public static final String MQTT_PING_ACTION = "cn.com.open.mqtt.PING";
	private DataUtils mDb;
	private int[] qualitiesOfService = { 0 };
	// receiver that notifies the Service when the phone gets data connection
	private NetworkConnectionIntentReceiver netConnReceiver;
	// taken from preferences
	String connectString;// = "tcp://10.96.142.93:1883";// tcp:10.96.142.93:1883
							// formal 10.100.133.146:1883
	private String brokerHostName = "";
	private String topicName = "";
	// receiver that wakes the Service up when it's time to ping the server
	private PingSender pingSender;
	// receiver that notifies the Service when the user changes data use
	// preferences
	private BackgroundDataChangeIntentReceiver dataEnabledReceiver;

	protected MqttClient mqttClient = null;
	String clientName;
	short keepAlive = ConstDefine.DEFAULT_KEEP_ALIVE;// 低耗网络，但是又需要及时获取数据，心跳30s
	public static String userSubNumber;
	public static String token;
	MessageHandler mHandler = MessageHandler.getInstance();

	public MqttClientHelper() {

	}

	/************************************************* service ---- start----- ******************************************************/

	@Override
	public void onCreate() {

		Log.i("Mqtt", "MqttClientHelper--  onCreate()");
		super.onCreate();

		// dataEnabledReceiver = new BackgroundDataChangeIntentReceiver();
		// registerReceiver(dataEnabledReceiver,
		// new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mBinder = new LocalBinder<MqttClientHelper>(this);
		Configuration.setResources(getResources());
		mDb = DataUtils.getInstance(this);
		connectString = this.getString(R.string.learningbar_mqtt_url);
		defineConnectionToBroker(connectString);

	}

	@Override
	public int onStartCommand(final Intent intent, int flags, final int startId) {
		Log.i("Mqtt", "MqttClientHelper--  onStartCommand()   startId = "
				+ startId);
		new Thread(new Runnable() {
			@Override
			public void run() {
				handleStart(intent, startId);
			}
		}, "MQTTservice").start();

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.i("Mqtt", "MqttClientHelper--  onRebind()");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("Mqtt", "MqttClientHelper--  onUnbind()");
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// disconnect immediately
		disconnectFromBroker();

		Log.i("Mqtt", "MqttClientHelper-- onDestroy()");

		// try not to leak the listener
		if (dataEnabledReceiver != null) {
			// unregisterReceiver(dataEnabledReceiver);
			dataEnabledReceiver = null;
		}
		if (mBinder != null) {
			mBinder.close();
			mBinder = null;
		}
	}

	/*
	 * Terminates a connection to the message broker.
	 */
	private void disconnectFromBroker() {
		// if we've been waiting for an Internet connection, this can be
		// cancelled - we don't need to be told when we're connected now
		try {
			if (netConnReceiver != null) {
				unregisterReceiver(netConnReceiver);
				netConnReceiver = null;
			}

			if (pingSender != null) {
				// unregisterReceiver(pingSender);
				pingSender = null;
			}
		} catch (Exception eee) {
			// probably because we hadn't registered it
			Log.e("Mqtt", "unregister failed", eee);
		}

		try {
			if (mqttClient != null) {
				Log.i("Mqtt", "MqttClientHelper-- mqttClient.disconnect()");
				mqttClient.disconnect();
			}
		} catch (MqttPersistenceException e) {
			Log.e("Mqtt", "disconnect failed - persistence exception", e);
		} finally {
			mqttClient = null;
		}

		// we can now remove the ongoing notification that warns users that
		// there was a long-running ongoing service running
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	/*
	 * Create a client connection object that defines our connection to a
	 * message broker server
	 */
	private void defineConnectionToBroker(String brokerHostName) {
		try {
			mqttClient = new com.ibm.mqtt.MqttClient(connectString);
			// register this client app has being able to receive messages
			mqttClient.registerAdvancedHandler(this);
		} catch (MqttException e) {
			// something went wrong!
			mqttClient = null;
			connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
			Log.e("Mqtt", "MqttException ::Invalid connection parameters");
		}
	}

	public void rebroadcastStatus() {
		String status = "";

		switch (connectionStatus) {
		case INITIAL:
			status = "Please wait";
			break;
		case CONNECTING:
			status = "Connecting...";
			break;
		case CONNECTED:
			status = "Connected";
			break;
		case NOTCONNECTED_UNKNOWNREASON:
			status = "Not connected - waiting for network connection";
			break;
		case NOTCONNECTED_USERDISCONNECT:
			status = "Disconnected";
			break;
		case NOTCONNECTED_DATADISABLED:
			status = "Not connected - background data disabled";
			break;
		case NOTCONNECTED_WAITINGFORINTERNET:
			status = "Unable to connect";
			break;
		}

	}

	/*
	 * Checks if the MQTT client thinks it has an active connection
	 */
	public boolean isAlreadyConnected() {
//		Log.e("Mqtt",
//				"----------------------isAlreadyConnected()-----------------------");
//		Log.d("Mqtt", "mqttClient = " + mqttClient);
//		Log.d("Mqtt", "isConnected() = " + mqttClient.isConnected());

		return ((mqttClient != null) && (mqttClient.isConnected() == true));
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		}

		return false;
	}

	/*
	 * (Re-)connect to the message broker
	 */
	private boolean connectToBroker() {

		try {
			if (userSubNumber == null) {
//				userSubNumber = DataUtils.getInstance(this).getLoginUserInfo().userBaseID; 
				return false;

			}

			Log.e("Mqtt", "connectToBroker() :userSubNumber = " + userSubNumber
					+ (int) Math.round(Math.random() * 89999999 + 10000000));
			if (MQTTConnectionStatus.CONNECTED.equals(connectionStatus))
				return false;
			// mqttClient.connect(generateClientId(), cleanStart,
			// keepAliveSeconds);

			mqttClient.connect(
					"U" + userSubNumber
							+ (int) Math.round(Math.random() * 89999 + 10000),
					true, keepAlive);

			// we are connected
			connectionStatus = MQTTConnectionStatus.CONNECTED;

			// we need to wake up the phone's CPU frequently enough so that the
			// keep alive messages can be sent
			// we schedule the first one of these now
			// scheduleNextPing();

			return true;
		} catch (MqttException e) {
			// something went wrong!

			connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

			Log.e("Mqtt", "Unable to connect" + e);

			// if something has failed, we wait for one keep-alive period before
			// trying again
			// in a real implementation, you would probably want to keep count
			// of how many times you attempt this, and stop trying after a
			// certain number, or length of time - rather than keep trying
			// forever.
			// a failure is often an intermittent network issue, however, so
			// some limited retry is a good idea
			scheduleNextPing();

			return false;
		}
	}

	/*
	 * Schedule the next time that you want the phone to wake up and ping the
	 * message broker server
	 */
	private void scheduleNextPing() {
		// When the phone is off, the CPU may be stopped. This means that our
		// code may stop running.
		// When connecting to the message broker, we specify a 'keep alive'
		// period - a period after which, if the client has not contacted
		// the server, even if just with a ping, the connection is considered
		// broken.
		// To make sure the CPU is woken at least once during each keep alive
		// period, we schedule a wake up to manually ping the server
		// thereby keeping the long-running connection open
		// Normally when using this Java MQTT client library, this ping would be
		// handled for us.
		// Note that this may be called multiple times before the next scheduled
		// ping has fired. This is good - the previously scheduled one will be
		// cancelled in favour of this one.
		// This means if something else happens during the keep alive period,
		// (e.g. we receive an MQTT message), then we start a new keep alive
		// period, postponing the next ping.

		PendingIntent pendingIntent = PendingIntent
				.getBroadcast(this, 0, new Intent(MQTT_PING_ACTION),
						PendingIntent.FLAG_UPDATE_CURRENT);

		// in case it takes us a little while to do this, we try and do it
		// shortly before the keep alive period expires
		// it means we're pinging slightly more frequently than necessary
		Calendar wakeUpTime = Calendar.getInstance();
		wakeUpTime.add(Calendar.SECOND, keepAlive);

		AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		aMgr.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(),
				pendingIntent);
	}

	/*
	 * Send a request to the message broker to be sent messages published with
	 * the specified topic name. Wildcards are allowed.
	 */
	private void subscribeToTopic(String topicName) {
		boolean subscribed = false;

		if (isAlreadyConnected() == false) {
			// quick sanity check - don't try and subscribe if we
			// don't have a connection

			Log.e("Mqtt", "Unable to subscribe as we are not connected");
		} else {
			try {
				String[] topics = { topicName };
				mqttClient.subscribe(topics, qualitiesOfService);
				Log.d("Mqtt", "MqttClientHelper-- subscribeToTopic()");
				subscribed = true;
			} catch (MqttNotConnectedException e) {
				Log.e("Mqtt", "subscribe failed - MQTT not connected", e);
			} catch (IllegalArgumentException e) {
				Log.e("Mqtt", "subscribe failed - illegal argument", e);
			} catch (MqttException e) {
				Log.e("Mqtt", "subscribe failed - MQTT exception", e);
			}
		}

		if (subscribed == false) {

			Log.e("Mqtt", "subscribeToTopic() :Unable to subscribe");

		}
	}

	synchronized void handleStart(Intent intent, int startId) {

		// before we start - check for a couple of reasons why we should stop
		if (mqttClient == null) {
			Log.d("Mqtt", "handleStart-- mqttClient == null");
			stopSelf();
			return;
		}

		// ConnectivityManager cm = (ConnectivityManager)
		// getSystemService(MqttClientHelper.this.CONNECTIVITY_SERVICE);
		// if (cm.getBackgroundDataSetting() == false) // respect the user's
		// // request not to use data!
		// {
		// connectionStatus = MQTTConnectionStatus.NOTCONNECTED_DATADISABLED;
		// Log.i("Mqtt", "Not connected - background data disabled");
		//
		// return;
		// }

		// the Activity UI has started the MQTT service - this may be starting
		// the Service new for the first time, or after the Service has been
		// running for some time (multiple calls to startService don't start
		// multiple Services, but it does call this method multiple times)
		// if we have been running already, we re-send any stored data
		rebroadcastStatus();

		// if the Service was already running and we're already connected - we
		// don't need to do anything
		if (isAlreadyConnected() == false) {
			// set the status to show we're trying to connect
			connectionStatus = MQTTConnectionStatus.CONNECTING;

			// 判断是否有网络
			if (isOnline()) {
				// 有网络 订阅Mqtt消息
				if (connectToBroker()) {
					
					Log.e("Mqtt", "handleStart-- connectToBroker()");
					subscribeToTopic("MSG_U" + userSubNumber);
				}
			} else {

				connectionStatus = MQTTConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;
				Log.i("Mqtt", "handleStart() : Waiting for network connection");
			}
		}

		// changes to the phone's network - such as bouncing between WiFi
		// and mobile data networks - can break the MQTT connection
		// the MQTT connectionLost can be a bit slow to notice, so we use
		// Android's inbuilt notification system to be informed of
		// network changes - so we can reconnect immediately, without
		// haing to wait for the MQTT timeout
		if (netConnReceiver == null) {
			netConnReceiver = new NetworkConnectionIntentReceiver();
			registerReceiver(netConnReceiver, new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION));

		}

		// creates the intents that are used to wake up the phone when it is
		// time to ping the server
		if (pingSender == null) {
			pingSender = new PingSender();
			// registerReceiver(pingSender, new IntentFilter(MQTT_PING_ACTION));
		}
	}

	private LocalBinder<MqttClientHelper> mBinder;

	public class LocalBinder<A> extends Binder {
		private WeakReference<A> mService;

		public LocalBinder(A service) {
			mService = new WeakReference<A>(service);
		}

		public A getService() {
			return mService.get();
		}

		public void close() {
			mService = null;
		}
	}

	/**************************************************** service ---- end----- *********************************************/

	/************************************************* send method ******************************************************/
	/**
	 * 发送文件
	 * 
	 */

	public void sendFile(String fromUser, String toUser, File uploadFile,
			String id, Context mContext) {
		final String path = Configuration
				.getHttpUrl(R.string.msg_url_sendImageFileMessage);
		final Map<String, String> params = new HashMap<String, String>();
		params.put("fromUser", fromUser);
		params.put("toUser", toUser);
		if( token == null)
			token = DataUtils.getInstance(this).getLoginUserInfo().token;
		params.put("token",token);
		params.put("id", id);
		final Context context = mContext;
		final String mId = id;

		final FormFile formFile = new FormFile(uploadFile, "uploadFile",
				"image/png");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SocketHttpRequester.post(path, params, formFile, mHandler,
							context);
				} catch (Exception e) {
					e.printStackTrace();
					Bundle bundle = new Bundle();
					bundle.putString("id", mId);
					bundle.putBoolean("isSuccess", false);
					if (context != null) {

						DataUtils.getInstance(context).updateSpecityItem(mId,
								Constants.MessageSendStatus.ERROR);

					}
					Message obtainMessage = mHandler.obtainMessage();
					obtainMessage.what = Constants.RESPONSE_SEND_MESSAGE;
					obtainMessage.setData(bundle);
					mHandler.sendMessage(obtainMessage);
				}
			}
		}).start();

	}

	/**
	 * 发送个人头像
	 * 
	 */

	public void sendUserIcon(String fromUser, File uploadFile, int toUrlId) {
		final String url = Configuration.getHttpUrl(toUrlId);
		final Map<String, String> params = new HashMap<String, String>();
		params.put("userBaseID", fromUser);
		params.put("businessId", fromUser);
		if( token == null)
			token = DataUtils.getInstance(this).getLoginUserInfo().token;
		params.put("token",token);
		final FormFile formFile = new FormFile(uploadFile, "uploadFile",
				"image/png");

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SocketHttpRequester.post(url, params, formFile, mHandler,
							null);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 测试用带有进度的文件传送
	 * 
	 * post = new HttpMultipartPost(context, filePath); post.execute();
	 * 尚未加token
	 */

	public void sendFilePro(Context context, int fromUser, int toUser,
			File uploadFile) {
		String path = Configuration
				.getHttpUrl(R.string.msg_url_sendImageFileMessage);
		HttpMultipartPost post = new HttpMultipartPost(context,
				uploadFile.getAbsolutePath(), path);
		post.execute();
	}

	/**
	 * 发送文本消息
	 * 
	 */

	public void sendMessage(int fromUser, int toUser, String content,
			String Id, Context mContext) {
		// basic.open.com.cn
		final String path = Configuration
				.getHttpUrl(R.string.msg_url_sendTextMessage);
		final Map<String, String> params = new HashMap<String, String>();
		if( token == null)
			token = DataUtils.getInstance(this).getLoginUserInfo().token;
		params.put("token",token);
		params.put("fromUser", String.valueOf(fromUser));
		params.put("toUser", String.valueOf(toUser));
		params.put("content", content);
		params.put("id", Id);
		final Context context = mContext;
		final String id = Id;

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SocketHttpRequester.sendPOSTRequest(path, params, "UTF-8",
							mHandler, context);
				} catch (Exception e) {

					e.printStackTrace();
					Bundle bundle = new Bundle();
					bundle.putString("id", id);
					bundle.putBoolean("isSuccess", false);
					if (context != null) {

						DataUtils.getInstance(context).updateSpecityItem(id,
								Constants.MessageSendStatus.ERROR);

					}
					Message obtainMessage = mHandler.obtainMessage();
					obtainMessage.what = Constants.RESPONSE_SEND_MESSAGE;
					obtainMessage.setData(bundle);
					mHandler.sendMessage(obtainMessage);

				}
			}
		}).start();

	}

	/**
	 * 获取未读消息
	 * 
	 */
	public void getNewMessge(String user) {
		final String path = Configuration
				.getHttpUrl(R.string.msg_url_getNewMessage);
		final Map<String, String> params = new HashMap<String, String>();
		if( token == null)
			token = DataUtils.getInstance(this).getLoginUserInfo().token;
		params.put("token",token);
		params.put("toUser", user);
		params.put("pageSize", "999");
		try {

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						SocketHttpRequester.sendPOSTRequest(path, params,
								"UTF-8", mHandler, null);
					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			}).start();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 提供DataUtils实例
	 */
	public DataUtils getObdInstance() {

		return mDb;
	}

	/************************************************* send method ******************************************************/

	/****************************************** asyntask ---- start----- ********************************************************/

	public class ParseMessage extends AsyncTask<String, String, String> {

		private String message;
		private ArrayList<FriendMessage> list;
		private HashMap<String, ArrayList<FriendMessage>> map;
		private MessageHandler mHandler;
		FriendMessage friendMessage;

		public ParseMessage(MessageHandler mHandler) {
			this.mHandler = mHandler;

		}

		@Override
		protected void onPostExecute(String result) {
			Message msg = mHandler.obtainMessage();
			msg.obj = friendMessage;

			switch (Integer.valueOf(friendMessage.getmMsgType())) {
			case 5:
				if ("80".equals(friendMessage.getmMsgCode())) {
					notifyUser("ticker", friendMessage.getmMsgAhthorID(),
							friendMessage.getmMsgContent());
				}
				if ("81".equals(friendMessage.getmMsgCode())) {
					notifyUser("ticker", friendMessage.getmMsgAhthorID(),
							friendMessage.getmMsgContent());
				}
				if (result.equals(Constants.FROM_UNREAD_NUMBER_VIEW)) {
					mHandler.sendEmptyMessage(Constants.UPDATE_MESSAGE_NUMBER);
				}
				if (result.equals(Constants.FROM_LETTER_LIST)) {
					msg.what = Constants.UPDATE_MESSAGE_LETTER;
					mHandler.sendMessage(msg);
				}
				break;
			case 2:

				if (result.equals(Constants.FROM_UNREAD_NUMBER_VIEW)) {
					notifyUser("ticker", friendMessage.getmMsgAhthorID(),
							friendMessage.getmMsgContent());
					mHandler.sendEmptyMessage(Constants.UPDATE_MESSAGE_NUMBER);
				}
				if (result.equals(Constants.FROM_CHAT_VIEW)) {
					// fromUser 为0，表示当前页面不是具体聊天页面，需要发送通知提醒用户
					if (Chat2Activity.fromUser == 0
							|| !friendMessage
									.getmMsgAhthorID()
									.equals(String
											.valueOf(Chat2Activity.fromUser)))
						notifyUser("ticker", friendMessage.getmMsgAhthorID(),
								friendMessage.getmMsgContent());
					msg.what = Constants.UPDATE_MESSAGE_CHAT;
					if (friendMessage
							.getmMsgAhthorID()
							.equals(String
									.valueOf(Chat2Activity.fromUser)))
						mHandler.sendMessage(msg);
				}
				if (result.equals(Constants.FROM_LETTER_LIST)) {
					notifyUser("ticker", friendMessage.getmMsgAhthorID(),
							friendMessage.getmMsgContent());
					msg.what = Constants.UPDATE_MESSAGE_LETTER;
					mHandler.sendMessage(msg);
				}
				if (result.equals(Constants.FROM_MESSAEHANDLER)) {
					notifyUser("ticker", friendMessage.getmMsgAhthorID(),
							friendMessage.getmMsgContent());
				}
				break;

			default:
				break;
			}
		}

		// 传入两个参数，第一个 要解析的json数据 ； 第二个 返回给onPostExecute 判断的依据
		@Override
		protected String doInBackground(String... params) {

			try {

				JSONObject subObject = JsonHelper.getSubObject(new JSONObject(
						params[0]), "payload");
				JSONArray subArrayObject = JsonHelper.getSubArrayObject(
						subObject, "items");

				/*
				 * 文本消息json格式 "message": { "content": "wangpan\n", "id":
				 * "k20131108045704076wpbe", "toUser": 1885, "fromUser": 1881,
				 * "status": 1, "code": 20, "type": 2, "url": "", "postTime":
				 * 1383901024076 }
				 */
				for (int i = 0; i < subArrayObject.length(); i++) {
					JSONObject object = (JSONObject) subArrayObject.get(i);
					JSONObject subObject2 = JsonHelper.getSubObject(object, "message");
					friendMessage = new FriendMessage();
					friendMessage.setmMsgSessionID(userSubNumber + "."
							+ subObject2.getString("fromUser"));
					friendMessage.setmMsgAhthorName(subObject2
							.getString("fromUserName"));
					friendMessage.setmMsgAhthorIcon(subObject2
							.getString("fromUserSmallFace"));
					friendMessage.setmMsgAcceptID(subObject2
							.getString("toUser"));
					friendMessage.setmMsgAhthorID(subObject2
							.getString("fromUser"));
					friendMessage.setmMsgType(subObject2.getString("type"));
					if (!"2".equals(friendMessage.getmMsgType())
							&& !"5".equals(friendMessage.getmMsgType())) {
						return "";
					}
					friendMessage.setmMsgCode(subObject2.getString("code"));
					friendMessage.setmMsgStatus(subObject2.getString("status"));
					// 发送到服务器的时间
					friendMessage.setReceiveTime(subObject2
							.getString("postTime"));
					// 发送到本地接收的时间
					friendMessage.setReceiveLocalTime(subObject2
							.getString("postTime"));
					friendMessage
							.setFromStatus(Constants.MessageFromType.FRIEND);
					if ("81".equals(friendMessage.getmMsgCode())) {
						friendMessage.setmMsgContent(MqttClientHelper.this.getString(R.string.msg_81_code));
					}else if (friendMessage.getmMsgCode().equals(
							String.valueOf(Constants.MESSAGE_TYPE_AUDIO))) {
						friendMessage.setmMsgContent("");
					} else if (friendMessage.getmMsgCode().equals(
							String.valueOf(Constants.MESSAGE_TYPE_IMG))) {
						friendMessage.setmMsgContent("");
						friendMessage.setmMsgSendStatus(String
								.valueOf(Constants.MessageSendStatus.SEND));
						friendMessage
								.setmMsgImgUrl(subObject2.getString("url"));
						friendMessage.setmMsgImgPath(subObject2
								.getString("smallurl"));
					} else if (friendMessage.getmMsgCode().equals(
							String.valueOf(Constants.MESSAGE_TYPE_TEXT))) {
						friendMessage.setmMsgContent((String) subObject2
								.get("content"));
						friendMessage.setmMsgSendStatus(String
								.valueOf(Constants.MessageSendStatus.SUCCESS));
					} else {
						friendMessage.setmMsgContent((String) subObject2
								.get("content"));
					}
					if("80".equals(friendMessage.getmMsgCode()))
					{
						mDb.replaceOrInsert(friendMessage);
					}
					else if("83".equals(friendMessage.getmMsgCode())){
						mDb.deleteMessageBySessionid(friendMessage.getmMsgSessionID());
					}
					else{
						mDb.addFriendMessage(friendMessage);// 每次接收到消息，存入本地数据库
					}
					

				}

			} catch (Exception e) {

				e.printStackTrace();
			}

			return params[1];
		}

	}

	/****************************************** asyntask ---- end----- ********************************************************/

	/****************************************** AdvancedCallback ---- start----- ************************************************************************/
	@Override
	public void published(int messageId) {
		Log.e("Mqtt", "----------------------发布完成-----------------------");
		Log.e("Mqtt", "消息ID：" + messageId);
	}

	@Override
	public void subscribed(int messageId, byte[] payload) {
		Log.e("Mqtt", "----------------------订阅完成-----------------------");
		Log.e("Mqtt", "消息ID：" + messageId);
		Log.e("Mqtt", "消息数据: " + new String(payload));
	}

	@Override
	public void unsubscribed(int messageId) {
		Log.e("Mqtt", "----------------------取消订阅完成-----------------------");
		Log.e("Mqtt", "消息ID：" + messageId);
	}

	@Override
	public void connectionLost() throws Exception {
		Log.e("Mqtt", "----------------------连接断开-----------------------");
		connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
		if (isOnline() && isAlreadyConnected() == false) {

			new Thread(new Runnable() {
				@Override
				public void run() {

					// we have an internet connection - have another try at
					// connecting
					if (connectToBroker()) {
						// we subscribe to a topic - registering to receive push
						// notifications with a particular key
						subscribeToTopic("MSG_U" + userSubNumber);
					}

				}
			}, "MQTTconnectionLost").start();

			subscribeToTopic("MSG_U" + userSubNumber);
		}
	}

	@Override
	public void publishArrived(String topicName, byte[] payload, int Qos,
			boolean retained) throws Exception {

		String mes = new String(payload);
		Log.e("Mqtt", "----------------------接收到消息-----------------------");
		Log.e("Mqtt", "消息数据: " + new String(payload));
		Log.e("Mqtt", "订阅主题: " + topicName);
		Log.e("Mqtt", "消息数据: " + new String(payload));
		Log.e("Mqtt", "消息级别(0,1,2): " + Qos);
		Log.e("Mqtt", "是否是实时发送的消息(false=实时，true=服务器上保留的最后消息): " + retained);

		Log.i("Mqtt", "打印当前对象： " + this);
		mHandler.setMqttClient(this);
		if (mes != null && mes.substring(0, 6).equalsIgnoreCase("100002")) {
			mHandler.sendEmptyMessage(Constants.NEW_MESSAGE);
			Log.e("Mqtt", "接收到100002消息，去获取新消息");
		}
	}

	/****************************************** AdvancedCallback ---- end----- *******************************************************/

	/****************************************** BroadcastReceiver ---- start----- ******************************************/
	/*
	 * Called in response to a change in network connection - after losing a
	 * connection to the server, this allows us to wait until we have a usable
	 * data connection again
	 */
	private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context ctx, Intent intent) {

			Log.e("Mqtt", "----NetworkConnectionIntentReceiver---");
			Log.d("Mqtt", "onReceive action =" + intent.getAction());
			// we protect against the phone switching off while we're doing this
			// by requesting a wake lock - we request the minimum possible wake
			// lock - just enough to keep the CPU running until we've finished
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			WakeLock wl = pm
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
			wl.acquire();

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (isOnline() && isAlreadyConnected() == false) {
						// we have an internet connection - have another try at
						// connecting
						if (connectToBroker()) {
							// we subscribe to a topic - registering to receive
							// push
							// notifications with a particular key
							subscribeToTopic("MSG_U" + userSubNumber);
						}
					}
				}
			}, "MQTTNetChange").start();

			// we're finished - if the phone is switched off, it's okay for the
			// CPU
			// to sleep now
			wl.release();
		}
	}

	/*
	 * Used to implement a keep-alive protocol at this Service level - it sends
	 * a PING message to the server, then schedules another ping after an
	 * interval defined by keepAliveSeconds
	 */
	public class PingSender extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Note that we don't need a wake lock for this method (even though
			// it's important that the phone doesn't switch off while we're
			// doing this).
			// According to the docs, "Alarm Manager holds a CPU wake lock as
			// long as the alarm receiver's onReceive() method is executing.
			// This guarantees that the phone will not sleep until you have
			// finished handling the broadcast."
			// This is good enough for our needs.

			try {
				mqttClient.ping();
			} catch (MqttException e) {
				// if something goes wrong, it should result in connectionLost
				// being called, so we will handle it there
				Log.e("Mqtt", "ping failed - MQTT exception", e);

				// assume the client connection is broken - trash it
				try {
					mqttClient.disconnect();
				} catch (MqttPersistenceException e1) {
					Log.e("Mqtt", "disconnect failed - persistence exception",
							e1);
				}

				// reconnect
				if (connectToBroker()) {
					subscribeToTopic(topicName);
				}
			}

			// start the next keep alive period
			// scheduleNextPing();
		}
	}

	private class BackgroundDataChangeIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			// we protect against the phone switching off while we're doing this
			// by requesting a wake lock - we request the minimum possible wake
			// lock - just enough to keep the CPU running until we've finished
			Log.e("Mqtt", "mqttClient connect  = " + mqttClient.isConnected());
			// PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			// WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
			// "MQTT");
			// wl.acquire();

			if (isOnline()) {
				// defineConnectionToBroker(brokerHostName);
				handleStart(intent, 0);
			} else {
				// user has disabled background data
				connectionStatus = MQTTConnectionStatus.NOTCONNECTED_DATADISABLED;

				// disconnect from the broker
				// disconnectFromBroker();
			}

			// we're finished - if the phone is switched off, it's okay for the
			// CPU to sleep now
			// wl.release();
		}
	}

	/****************************************** BroadcastReceiver ---- end----- ********************************************************/

	private void notifyUser(String ticker, String title, String body) {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.putExtra("notification", 20131128);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent
				.setAction(Constants.Intent_Action.OB_RECIVER_MESSAGE_ACTION);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new NotificationCompat.Builder(this)
				.setContentTitle(
						MqttClientHelper.this.getString(R.string.msg_title))
				.setContentText(
						MqttClientHelper.this.getString(R.string.msg_content))
				.setTicker(MqttClientHelper.this.getString(R.string.msg_ticker))
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(null).setWhen(System.currentTimeMillis())
				.setContentIntent(contentIntent).build();
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.ledARGB = Color.MAGENTA;
		nm.cancel(R.string.app_version_name);

		nm.notify(R.string.app_version_name, notification);
	}

}
