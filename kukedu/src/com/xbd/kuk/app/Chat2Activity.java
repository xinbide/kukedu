package com.xbd.kuk.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.xbd.kuk.MainApp;
import com.xbd.kuk.R;
import com.xbd.kuk.datastart.DataUtils;
import com.xbd.kuk.model.ChatMsgEntity;
import com.xbd.kuk.model.ChatMsgViewAdapter;
import com.xbd.kuk.model.FriendMessage;
import com.xbd.kuk.model.MessageHandler;
import com.xbd.kuk.model.MqttClientHelper;
import com.xbd.kuk.model.User;
import com.xbd.kuk.ui.KUKEditText;
import com.xbd.kuk.ui.KUKFaceView;
import com.xbd.kuk.util.Constants;
import com.xbd.kuk.util.ImageUtil;
import com.xbd.kuk.util.KUKUtil;
import com.xbd.kuk.util.UIUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;


/**
 * 
 * @author geniuseoe2012
 *  更多精彩，请关注我的CSDN博客http://blog.csdn.net/geniuseoe2012
 *  android开发交流群：200102476
 */
public class Chat2Activity extends Activity implements OnClickListener
	, OnItemClickListener, OnTouchListener,
	com.xbd.kuk.model.MessageHelperCallBack, OnScrollListener,
	KUKFaceView.OnFaceListClickListener {
    /** Called when the activity is first created. */

	//---------------------------
	ImageView 	chat_img_more;
	ImageView 	chat_img_audio;
	ImageView 	chat_img_keyboard;
	KUKEditText chat_edit_msg;
	Button 		chat_btn_msg_send;
	Button 		chat_btn_audio_send;
	ImageView	chat_img_face;
	ImageView	chat_img_camera;
	ImageView	chat_img_album;
	
	LinearLayout chat_layout_text;
	LinearLayout chat_layout_audio;
	View chat_layout_more;
	View chat_layout_face;
	View chat_layout_main;
	KUKFaceView mGridView;
	ListView mChatMsgListView;
	
	private View mImgPreviewLayout;// 查看图片的视图
	private Button mResetButton; // 重新选择图片按钮
	private Button mSubmitBtn; // 确认选择图片的按钮
	private ImageView mImagePreview;
	
	private View mRecorderView;
	private TextView mSoundText;
	

	private ListView mListView;
	//private ChatMsgViewAdapter mAdapter;
	//private OBLFriendChatMsgListAdapter mChatMsgListAdapter;
	private ChatMsgViewAdapter mChatMsgListAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private TextView mTxtChatTitle;
	//---------------------------
	private InputMethodManager mMethodManager;
	private MediaRecorder mMediaRecorder; // 录音操作API
	private Timer mTimer; // 处理录音时间的定时器
	private int mOrientation;// 图片旋转的角度
	
	public static int fromUser;
	private String mFriendicon;
	private String mFriendName;
	private String mSessionId;
	private String toUserId = null;
	private String fromUserId = null;
	private DataUtils obd;

	private String mPicPath;
	private String mAudioPath;
	private ArrayList<FriendMessage> mFriendMessages;
	private ArrayList<FriendMessage> mTempFriendMessages;	

	private static int firstId = 0;
	private static final int limitNum = 15;
	public static final int Request_code = 10001;
	private static final String picType = "img";
	
	private static final int REQUEST_CODE_ALBUM = 10011;
	private static final int REQUEST_CODE_CAMERA = 10012;
	private static final int MESSAGE_CODE_RECODER = 10013;
	//---------------------------

	public MessageHandler mHandler = MessageHandler.getInstance();
	public static MqttClientHelper mChatClient;// 发送消息客户端
	
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat2);
        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        getIntentData();
        initView();
        
        obd = DataUtils.getInstance(this);
        initData();

//		mHandler.setMessageCallBack(this);
    }
    
    private void getIntentData() {
		mFriendicon = getIntent().getStringExtra("friendicon");
		mFriendName = getIntent().getStringExtra("friendName");
		toUserId = getIntent().getStringExtra("toUserId");
		fromUserId = getIntent().getStringExtra("fromUserId");
		// 对话双方的id
		mSessionId = getIntent().getStringExtra("sessionId");
		if (fromUserId != null) {
			fromUser = Integer.valueOf(fromUserId);
			Log.d("Mqtt", "--------------------- fromUser = " + fromUser);
		} else {
			if (mSessionId != null) {
				if (mSessionId.indexOf(".") > 0) {
					fromUserId = mSessionId.substring(
							mSessionId.indexOf(".") + 1, mSessionId.length());
					fromUser = Integer.valueOf(fromUserId);
					toUserId = mSessionId.substring(0, mSessionId.indexOf("."));
				}

				Log.d("Mqtt", "--------------------- fromUser = " + fromUser);
			}

		}

	}
    
    public void initView()
    {
    	mListView = (ListView) findViewById(R.id.listview);
    	mTxtChatTitle = (TextView) findViewById(R.id.txtChatTitle);
    	mTxtChatTitle.setText(mFriendName);
    	//----- -----
    	// 软键盘管理
    	mMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	chat_img_more = (ImageView) findViewById(R.id.chat_img_more);			// 更多按钮
    	chat_img_audio = (ImageView) findViewById(R.id.chat_img_audit);			// 音频按钮
    	chat_img_keyboard = (ImageView) findViewById(R.id.chat_img_keyboard);	// 文字按钮
    	chat_img_face = (ImageView) findViewById(R.id.chat_img_face);
    	chat_img_camera = (ImageView) findViewById(R.id.chat_img_camera);
    	chat_img_album = (ImageView) findViewById(R.id.chat_img_album);
    	
    	chat_edit_msg = (KUKEditText) findViewById(R.id.chat_edit_msg);			// 文字聊天的文本编辑框
    	
    	chat_btn_msg_send = (Button) findViewById(R.id.chat_btn_msg_send);
    	
    	chat_btn_audio_send = (Button) findViewById(R.id.chat_btn_audio_send);
    	chat_btn_msg_send = (Button) findViewById(R.id.chat_btn_msg_send);
    	
    	chat_layout_main = findViewById(R.id.chat_layout_main);
    	chat_layout_text = (LinearLayout) findViewById(R.id.ChatTextLayout);		// 文字聊天是显示的部分
    	chat_layout_audio = (LinearLayout) findViewById(R.id.ChatAudioLayout);	// 音频聊天的显示部分
    	chat_layout_more = findViewById(R.id.chatMoreLayout);	// 点击更多的弹出面板
    	chat_layout_face = findViewById(R.id.faceListLayout);	// 展示表情的地方
    	mGridView = (KUKFaceView) findViewById(R.id.faceGridView);
    	
		mChatMsgListView = (ListView) findViewById(R.id.chatMsgListView);
		
		// 图片选取 
		mImgPreviewLayout = findViewById(R.id.ImagePreviewMainLayout);
		// 重新选图按钮
		mResetButton = (Button) findViewById(R.id.ResetImageBtn);
		// 提交按钮
		mSubmitBtn = (Button) findViewById(R.id.SubmitImageBtn);
		// 图片展示ImageView
		mImagePreview = (ImageView) findViewById(R.id.previewImage);
		// 录音展示的图像
		mRecorderView = findViewById(R.id.AudioRecorderView);
		mSoundText = (TextView) findViewById(R.id.soundRateText);
    	//----- -----
    	chat_btn_msg_send.setOnClickListener(this);
		mGridView.setFaceItemClickListener(this);
		mChatMsgListView.setOnScrollListener(this);
		mChatMsgListView.setOnItemClickListener(this);
		
		chat_edit_msg.setOnClickListener(this);
		chat_img_audio.setOnClickListener(this);
		chat_img_keyboard.setOnClickListener(this);
		chat_img_face.setOnClickListener(this);
		chat_img_camera.setOnClickListener(this);
		chat_img_album.setOnClickListener(this);
		chat_img_more.setOnClickListener(this);
		chat_btn_audio_send.setOnTouchListener(this);
		chat_btn_msg_send.setOnClickListener(this);
		chat_btn_msg_send.setClickable(false);

		mSubmitBtn.setOnClickListener(this);
		mResetButton.setOnClickListener(this);
    	//----- -----
    	
    	chat_edit_msg.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (chat_edit_msg != null && chat_btn_msg_send != null) {
					if (s.length() == 0) {
						chat_btn_msg_send.setClickable(false);
					} else if (s.length() > 0) {
						chat_btn_msg_send.setClickable(true);
					}

				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}});
    	// 隐藏键盘
    	hideSoftInputKeyboard(chat_edit_msg);
    }
    
    private void hideSoftInputKeyboard(View view) {
		if (mMethodManager.isActive()) {
			mMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
    
    //private String[]msgArray = new String[]{"有大吗", "有！你呢？", "我也有", "那上吧", "打啊！你放大啊", "你tm咋不放大呢？留大抢人头那！Cao的。你个菜b", "2B不解释", "尼滚....",};
    private String[]msgArray = new String[]{"你好", "我很好！你呢？", "我不好",};
    //private String[]dataArray = new String[]{"2014-07-16 18:00", "2012-09-01 18:10", "2012-09-01 18:11", "2012-09-01 18:20", "2012-09-01 18:30", "2012-09-01 18:35", "2012-09-01 18:40", "2012-09-01 18:50"}; 
    private String[]dataArray = new String[]{"2014-07-16 18:00", "2012-09-01 18:10", "2012-09-01 18:11"}; 
    //private final static int COUNT = 8;
    private final static int COUNT = 3;
    public void initData()
    {
    	mTxtChatTitle.setText("小黑");
    	for(int i = 0; i < COUNT; i++)
    	{
    		ChatMsgEntity entity = new ChatMsgEntity();
    		entity.setDate(dataArray[i]);
    		if (i % 2 == 0)
    		{
    			entity.setName("小黑");
    			entity.setMsgType(true);
    		}else{
    			entity.setName("sun");
    			entity.setMsgType(false);
    		}
    		
    		entity.setText(msgArray[i]);
    		mDataArrays.add(entity);
    	}

    	mChatMsgListAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mChatMsgListAdapter);
		
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
//		case R.id.btn_send:
//			send();
//			break;
//		case R.id.btn_back:
//			finish();
//			break;
			
		case R.id.chat_img_audit://ChatMsgAudioImg:
			// 隐藏文字发送功能，现实语音发送功能
			chat_img_audio.setVisibility(View.GONE);
			chat_img_keyboard.setVisibility(View.VISIBLE);
			chat_edit_msg.setVisibility(View.GONE);
			chat_layout_audio.setVisibility(View.VISIBLE);
			//
			// // 隐藏更多功能弹出框
			chat_img_more.setVisibility(View.GONE);
			// // 隐藏表情列表
			chat_layout_face.setVisibility(View.GONE);
			// // 隐藏键盘输入框
			 hideSoftInputKeyboard(chat_edit_msg);
			break;
		case R.id.chat_img_keyboard://ChatMsgKeyboardImg:
			// // 隐藏语音发送功能，现实文字输入发送功能
			chat_img_audio.setVisibility(View.VISIBLE);
			chat_img_keyboard.setVisibility(View.GONE);
			chat_edit_msg.setVisibility(View.VISIBLE);
			chat_layout_audio.setVisibility(View.GONE);
			//
			hideSoftInputKeyboard(chat_edit_msg);
			chat_edit_msg.clearFocus();
			mMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_IMPLICIT_ONLY);
			break;
		case R.id.chat_img_album://ChatAlbumImg:
			// 相册选择图片按钮
			hideSoftInputKeyboard(chat_edit_msg);
			callSystemAlbum();
			chat_layout_more.setVisibility(View.GONE);
			break;
		case R.id.chat_img_face://ChatFaceImg:
			// 点击表情图标，则隐藏软键盘
			hideSoftInputKeyboard(chat_edit_msg);
			// 如果 当前待操作的功能是语音发送，则隐藏语音发送功能，现实文字输入的功能。
			chat_img_audio.setVisibility(View.VISIBLE);
			chat_img_keyboard.setVisibility(View.GONE);
			chat_edit_msg.setVisibility(View.VISIBLE);
			chat_layout_audio.setVisibility(View.GONE);
			chat_layout_more.setVisibility(View.GONE);

			// 如果表情列表现实，则隐藏，如果隐藏，则显示
			if (chat_layout_face.getVisibility() == View.GONE) {
				chat_layout_face.setVisibility(View.VISIBLE);
			} else {
				chat_layout_face.setVisibility(View.GONE);
			}

			break;
		case R.id.chat_img_camera://ChatCameraImg:
			// 点相机图标，则隐藏软键盘，调用系统相机。
			hideSoftInputKeyboard(chat_edit_msg);
			callSystemCamera();
			chat_layout_more.setVisibility(View.GONE);
			break;
		case R.id.chat_img_more://ChatMsgMoreImg:
			// 如果更多功能视图隐藏，则现实，如果现实则隐藏
			if (chat_layout_more.getVisibility() == View.VISIBLE) {
				chat_layout_more.setVisibility(View.GONE);
			} else {
				chat_layout_more.setVisibility(View.VISIBLE);
			}

			break;

		case R.id.chat_btn_msg_send://sendChatMsgBtn:
			// 文字聊天按钮按钮的发送
			sendChatMessage(Constants.MESSAGE_TYPE_TEXT);
			break;

		case R.id.chat_edit_msg://ChatMsgEditText:
			// 点击文字输入框，隐藏表情列表，弹出软键盘
			if (chat_layout_face.getVisibility() == View.VISIBLE) {
				chat_layout_face.setVisibility(View.GONE);
			}
			break;
		case R.id.ResetImageBtn:// 重选图片按钮
			setHideActionBar(false);
			if (mPicPath != null) {
				// 删除文件夹中的放弃的图片
				File deleteFile = new File(mPicPath);
				deleteFile.delete();
			}
			mPicPath = null;
			chat_layout_main.setVisibility(View.VISIBLE);
			mImgPreviewLayout.setVisibility(View.GONE);
			break;
		case R.id.SubmitImageBtn: // 确认图片选择按钮
			// 图片发送按钮
			setHideActionBar(false);
			chat_layout_main.setVisibility(View.VISIBLE);
			mImgPreviewLayout.setVisibility(View.GONE);
			sendChatMessage(Constants.MESSAGE_TYPE_IMG);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CODE_CAMERA) {
				setHideActionBar(true);
				chat_layout_main.setVisibility(View.GONE);
				mImgPreviewLayout.setVisibility(View.VISIBLE);

				// Uri uri = data.getData();
				// mPicPath = getSelectPicture(uri);
				// mPicPath = handleCameraPicture(mPicPath);
				mImagePreview.setImageBitmap(loadBitmap(mPicPath));
			} else if (requestCode == REQUEST_CODE_ALBUM) {
				setHideActionBar(true);
				chat_layout_main.setVisibility(View.GONE);
				mImgPreviewLayout.setVisibility(View.VISIBLE);
				Bitmap bm = null;
				// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				ContentResolver resolver = getContentResolver();
				try {

					Uri originalUri = data.getData(); // 获得图片的uri

					bm = MediaStore.Images.Media.getBitmap(resolver,
							originalUri); // 显得到bitmap图片
					File tempImg = ImageUtil.getInstance(
							Chat2Activity.this)
							.bornSolePath(mSessionId, picType,
									Constants.MessageFromType.MINE, null);
					mPicPath = tempImg.getAbsolutePath();
					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(mPicPath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					try {
						fOut.flush();
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					bm.recycle();

					mImagePreview.setImageBitmap(loadBitmap(mPicPath));

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (requestCode == Request_code) {
				int backPosition = data.getExtras().getInt("backPosition", 0);
				FriendMessage message = mFriendMessages.get(backPosition);
				FriendMessage newMessage = obd.queryByID(message.getId());
				if (newMessage != null) {
					mFriendMessages.get(backPosition).setmMsgImgPath(
							newMessage.getmMsgImgPath());
					mFriendMessages.get(backPosition).setmMsgImgUrl(
							newMessage.getmMsgImgUrl());
					mChatMsgListAdapter.notifyDataSetChanged();
				}

			}
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent == mChatMsgListView) {
			FriendMessage friendMessage = (FriendMessage) parent.getAdapter().getItem(position);
			if (Integer.valueOf(friendMessage.getmMsgCode()) == Constants.MESSAGE_TYPE_AUDIO) {
				playAudio(friendMessage.getmMsgVideoUrl());
			}

		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			v.setBackgroundResource(R.drawable.img_btn_login_pressed);
			// 按下按钮开始录音
			startRecoderAudio();
			break;
		case MotionEvent.ACTION_UP:
			v.setBackgroundResource(R.drawable.img_btn_exit_pressed);
			// 放开按钮开始发送音频
			sendRecoderAudio(mAudioPath);
			break;
		}
		return true;
	}
	
	@Override
	public String getNewMessage(Message msg) {

		switch (msg.what) {
		case MESSAGE_CODE_RECODER:
			// 显示当前的录音时间
			mSoundText.setText(msg.obj.toString());
			break;
		case Constants.NEW_MESSAGE:
			Log.d("Mqtt", "ChatActivity 开始获取新消息");
			mChatClient.getNewMessge(MqttClientHelper.userSubNumber);
			break;
		case Constants.DEAL_NEW_MESSAGE:
			Log.d("Mqtt", "ChatActivity ---start parse---");
			mChatClient.new ParseMessage(mHandler).execute(new String[] {(String) msg.obj, Constants.FROM_CHAT_VIEW });
			break;
		case Constants.DEAL_TOKEN_LOCKED:
			showNoTokenDialog();
			break;
		case Constants.UPDATE_MESSAGE_CHAT:
			Log.d("Mqtt", "ChatActivity ---receive message---");
			// 成功接收对方的消息
			FriendMessage friendMessage = (FriendMessage) msg.obj;
			obd.updateMessageBySessionid(friendMessage.getmMsgSessionID());
			if (Integer.valueOf(friendMessage.getmMsgCode()) == Constants.MESSAGE_TYPE_IMG) {
				// 解决图片一次下载成功后不再重复下载
				FriendMessage newFriendMessage = obd.queryspeMessage(friendMessage.getmMsgSessionID(), friendMessage.getFromStatus(), Long.valueOf(friendMessage.getReceiveTime()));
				if (newFriendMessage != null) {
					mFriendMessages.add(newFriendMessage);
				} else {
					mFriendMessages.add(friendMessage);
				}
			} else {
				mFriendMessages.add(friendMessage);
			}

			mChatMsgListAdapter.notifyDataSetChanged();
			mChatMsgListView.setSelection(mFriendMessages.size());

			break;
		case Constants.RESPONSE_SEND_MESSAGE:
			// 自己的消息发送成功后进行状态的更新
			Bundle getBundle = (Bundle) msg.getData();
			// 具体的消息Id
			String id = getBundle.getString("id");
			Log.d("receive", id);
			boolean success = getBundle.getBoolean("isSuccess");
			// 消息发送成功的处理
			if (success) {
				for (int i = mFriendMessages.size() - 1; i >= 0; i--) {
					FriendMessage item = mFriendMessages.get(i);

					if (item.getId() != null && item.getId().equals(id)) {
						// 如果那个消息发送成功，刷新界面，消失发送滚动框
						mFriendMessages
								.get(i)
								.setmMsgSendStatus(
										String.valueOf(Constants.MessageSendStatus.SUCCESS));

						mChatMsgListAdapter.notifyDataSetChanged();
						mChatMsgListView.setSelection(mFriendMessages.size());
						break;
					}
				}

			} else {
				// 如果发送不成功，显示发送失败的界面
				for (int i = mFriendMessages.size() - 1; i >= 0; i--) {
					FriendMessage item = mFriendMessages.get(i);
					if (item.getId() != null && item.getId().equals(id)) {
						mFriendMessages
								.get(i)
								.setmMsgSendStatus(
										String.valueOf(Constants.MessageSendStatus.ERROR));
						mChatMsgListAdapter.notifyDataSetChanged();
						mChatMsgListView.setSelection(mFriendMessages.size());
						break;
					}

				}
			}

		}

		return null;
	}

	@Override
	public void onFaceClick(Drawable drawable, String unicode) {
		int idx = chat_edit_msg.getSelectionStart();
		Editable edText = chat_edit_msg.getEditableText();
		edText.insert(idx, KUKUtil.convertUnicodeToFace(unicode));
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (view.getFirstVisiblePosition() == 0) {
				int[] listView = new int[4];
				int[] item = new int[4];
				view.getLocationOnScreen(listView);
				view.getChildAt(0).getLocationOnScreen(item);
				if (listView[1] == item[1]) {
					showLoadingProgress(this, R.string.loading_tips);
					mTempFriendMessages = (ArrayList<FriendMessage>) obd
							.queryMsgHistoryBySessionidlimit(mSessionId, firstId, limitNum);
					if (mTempFriendMessages.size() > 0) {
						for (int i = mTempFriendMessages.size() - 1; i >= 0; i--) {
							mFriendMessages.add(0, mTempFriendMessages.get(i));
						}
						firstId = mTempFriendMessages.get(
								mTempFriendMessages.size() - 1).getIntegerId();
						mTempFriendMessages.clear();
						mChatMsgListAdapter.notifyDataSetChanged();

					}
					this.cancelLoadingProgress();
				}

			}

			break;

		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		fromUser = Integer.valueOf(fromUserId);

	}

	@Override
	protected void onStop() {
		super.onStop();
		fromUser = 0;
	}
	//------------------------------------
	
	/**
	 * 调用相册选择图片
	 */
	private void callSystemAlbum() {
		Intent i = new Intent();

		i.setAction(Intent.ACTION_PICK);
		i.setType("image/*");
		startActivityForResult(Intent.createChooser(i, "相册"),
				REQUEST_CODE_ALBUM);

	}

	/**
	 * 调用系统相机
	 */
	private void callSystemCamera() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// 设置照片的默认保存路径
		File tempImg = ImageUtil.getInstance(Chat2Activity.this).bornSolePath(
				mSessionId, picType, Constants.MessageFromType.MINE, null);

		if (tempImg != null) {
			// 预先设置图片的保存路径
			mPicPath = tempImg.getAbsolutePath();
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImg));
		}

		startActivityForResult(i, REQUEST_CODE_CAMERA);

	}
	
	public void playAudio(String path) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaPlayer.start();
	}
	private int mRecorderTime;
	/**
	 * 开始录音
	 */
	public void startRecoderAudio() {
		// 录音画面开始展现
		mRecorderView.setVisibility(View.VISIBLE);

		mAudioPath = getRecorderFile().getAbsolutePath();
		// 创造录音路径

		mTimer = new Timer();

		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频来源（麦可）
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); // 音频输出格式
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 音频编码格式
		mMediaRecorder.setOutputFile(mAudioPath); // 音频输出路径

		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mMediaRecorder.start();
		// 计算录音时间
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_CODE_RECODER, ++mRecorderTime));
			}
		}, 1000, 1000);

	}

	/**
	 * 发送录音
	 */

	public void sendRecoderAudio(String filePath) {
		mRecorderView.setVisibility(View.GONE);

		// 取消录音
		mMediaRecorder.stop();
		mMediaRecorder.release();
		// 当前录音加入到录音列表中
		FriendMessage friendMessage = new FriendMessage();
		friendMessage.setmMsgVideoDuration(mRecorderTime);
		friendMessage.setmMsgVideoUrl(filePath);
		friendMessage.setmMsgCode(String.valueOf(Constants.MESSAGE_TYPE_AUDIO));
		mFriendMessages.add(friendMessage);
		mChatMsgListAdapter.notifyDataSetChanged();
		mChatMsgListView.setSelection(mFriendMessages.size());
		mSoundText.setText(String.valueOf(0));
		// 取消时间定时期，并初始化录音时间
		mTimer.cancel();
		mRecorderTime = 0;

	}

	/**
	 * 根据当前时间声称音频文件
	 * 
	 * @return
	 */
	public File getRecorderFile() {
		File directoryFile = new File(Environment.getExternalStorageDirectory()
				+ "/open/");
		if (!directoryFile.exists()) {

			directoryFile.mkdir();

		}
		File file = null;
		try {
			file = File.createTempFile("audio" + new Date().getTime(), ".amr",
					directoryFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	private Bitmap handleCameraPicture(String urlPicPath) {

		Bitmap bitmap = getBitmapFormStream(urlPicPath);

		Bitmap bMapRotate;
		if (mOrientation != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(mOrientation);
			bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} else
			bMapRotate = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),
					bitmap.getHeight(), true);

		return bMapRotate;

	}
	
	public Bitmap getBitmapFormStream(String path) {

		File file = new File(path);

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return BitmapFactory.decodeStream(fileInputStream);

	}

	private String rewriteBitmap(Bitmap bitmap, String path) {
		FileOutputStream out;

		try {
			File imgFile = new File(path);
			out = new FileOutputStream(imgFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return path;
	}
	
	public Bitmap loadBitmap(String imgpath) {

		Bitmap bm = getBitmapFormStream(imgpath);

		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imgpath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			// 读取图片中相机方向信息
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			// 计算旋转角度
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
			}
		}
		if (digree != 0) {
			// 旋转图片
			Matrix m = new Matrix();
			m.postRotate(digree);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
					m, true);
		}
		return bm;
	}
	
	//---------------------
	
	public void setHideActionBar(boolean isHide) {
//		mActionBar.setHideActionBar(isHide);
	}
	
	private void sendChatMessage(int messageType){
		// 文本消息的内容
		String message = chat_edit_msg.getText().toString();
		FriendMessage friendMessage = new FriendMessage();
		// 设置消息的来源
		friendMessage.setFromStatus(Constants.MessageFromType.MINE);
		// 设置消息的内容
		if (messageType == Constants.MESSAGE_TYPE_TEXT) {
			// 设置消息的类型
			friendMessage.setmMsgCode(String
					.valueOf(Constants.MESSAGE_TYPE_TEXT));
			// message = Base64.encodeToString(message.getBytes(),
			// Base64.DEFAULT);
			message = chat_edit_msg.getTextString();

			friendMessage.setmMsgContent(message);
		} else if (messageType == Constants.MESSAGE_TYPE_IMG) {
			// 设置消息的类型
			friendMessage.setmMsgCode(String
					.valueOf(Constants.MESSAGE_TYPE_IMG));
			friendMessage.setmMsgContent("");
			// 设置大图的默认保存位置
			friendMessage.setmMsgImgUrl(mPicPath);
		} else if (messageType == Constants.MESSAGE_TYPE_AUDIO) {
			// 设置消息的类型
			friendMessage.setmMsgCode(String
					.valueOf(Constants.MESSAGE_TYPE_AUDIO));
		}
		// 自己发送的消息自动置于可读状态
		friendMessage.setmMsgStatus(String
				.valueOf(Constants.MessageReadStatus.SUCCESS));
		friendMessage.setmMsgSendStatus(String
				.valueOf(Constants.MessageSendStatus.SEND));
		Long sendTime = System.currentTimeMillis();
		// 设置消息的本地时间
		friendMessage.setReceiveLocalTime(String.valueOf(sendTime));
		// 设置消息的网络时间
		friendMessage.setReceiveTime(String.valueOf(sendTime));
		// 设置消息的SessionID
		friendMessage.setmMsgSessionID(mSessionId);
		friendMessage.setmMsgAcceptID(toUserId);
		friendMessage.setmMsgAhthorID(fromUserId);
		friendMessage.setmMsgAhthorName(mFriendName);
		friendMessage.setmMsgAhthorIcon(mFriendicon);
//		obd.addFriendMessage(friendMessage);
		Log.e("Mqtt", " message = " + message);
		// mChatClient.sendMessage(1185, 1181, message);
		// 立即查询该消息的唯一ID，做为消息发送成功的比照ID
		// 为了获取生成的Id
//		FriendMessage newFriendMessage = obd.queryspeMessage(mSessionId, Constants.MessageFromType.MINE, sendTime);
		FriendMessage newFriendMessage = null;
		if (newFriendMessage != null) {
			mFriendMessages.add(newFriendMessage);
		} else {
			//UIUtils.getInstance().showToast(OBLFriendPrivateLetterChatActivity.this, "发言数据无法保存数据库，发送失败");
			Toast.makeText(this, "发言数据无法保存数据库，发送失败", Toast.LENGTH_SHORT);
			return;
		}

		// 调用消息发送的网络接口
//		if (messageType == Constants.MESSAGE_TYPE_TEXT) {
//			if (chat_edit_msg != null) {
//				if (chat_edit_msg.getFormatTextString().length() > maxNum) {
//					if (chat_edit_msg.getFormatTextString().length() % maxNum == 0) {
//						for (int i = 0; i < (chat_edit_msg.getFormatTextString()
//								.length() / maxNum); i++) {
//							if (i == 0) {
//								mChatClient
//										.sendMessage(
//												Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//												Integer.valueOf(fromUserId),
//												chat_edit_msg.getFormatTextString().substring(0, maxNum * 1 - 1),
//												newFriendMessage.getId(),
//												OBLFriendPrivateLetterChatActivity.this);
//							} else {
//								mChatClient
//										.sendMessage(
//												Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//												Integer.valueOf(fromUserId),
//												chat_edit_msg.getFormatTextString().substring(i * maxNum, (i + 1) * maxNum - 1),
//												newFriendMessage.getId(),
//												OBLFriendPrivateLetterChatActivity.this);
//							}
//						}
//					} else {
//						for (int i = 0; i < (chat_edit_msg.getFormatTextString().length() / maxNum) + 1; i++) {
//							if (i == 0) {
//								mChatClient
//										.sendMessage(
//												Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//												Integer.valueOf(fromUserId),
//												chat_edit_msg.getFormatTextString().substring(0, maxNum * 1 - 1),
//												newFriendMessage.getId(),
//												OBLFriendPrivateLetterChatActivity.this);
//							} else if (i == chat_edit_msg.getFormatTextString() .length() / maxNum) {
//								mChatClient
//										.sendMessage(
//												Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//												Integer.valueOf(fromUserId),
//												chat_edit_msg.getFormatTextString().substring(i * maxNum,
//														chat_edit_msg.getFormatTextString().length()),
//												newFriendMessage.getId(),
//												OBLFriendPrivateLetterChatActivity.this);
//							} else {
//								mChatClient
//										.sendMessage(
//												Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//												Integer.valueOf(fromUserId),
//												chat_edit_msg
//														.getFormatTextString()
//														.substring(
//																i * maxNum - 1,
//																(i + 1)
//																		* maxNum
//																		- 1),
//												newFriendMessage.getId(),
//												OBLFriendPrivateLetterChatActivity.this);
//							}
//						}
//					}
//
//				} else {
//					mChatClient
//							.sendMessage(
//									Integer.valueOf(((OBMainApp) getApplicationContext()).currentUser.userBaseID),
//									Integer.valueOf(fromUserId), message,
//									newFriendMessage.getId(),
//									OBLFriendPrivateLetterChatActivity.this);
//				}
//			}
//
//		} else if (messageType == Constants.MESSAGE_TYPE_IMG) {
//			if (mPicPath != null) {
//				File uploadFile = new File(mPicPath);
//				mChatClient
//						.sendFile(
//								((OBMainApp) getApplicationContext()).currentUser.userBaseID,
//								fromUserId, uploadFile,
//								newFriendMessage.getId(),
//								OBLFriendPrivateLetterChatActivity.this);
//			}
//
//		}
		
		String contString = chat_edit_msg.getText().toString();
		if (contString.length() > 0)
		{
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("sun");
			entity.setMsgType(false);
			entity.setText(contString);
			
			mDataArrays.add(entity);
//			mAdapter.notifyDataSetChanged();
//			chat_edit_msg.setText("");
//			mListView.setSelection(mListView.getCount() - 1);
		}

		// 刷新显示的界面
		mChatMsgListAdapter.notifyDataSetChanged();
		chat_edit_msg.setText("");
		mChatMsgListView.setSelection(mFriendMessages.size());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (mImgPreviewLayout.getVisibility() == View.VISIBLE) {
				chat_layout_main.setVisibility(View.VISIBLE);
				mImgPreviewLayout.setVisibility(View.GONE);
				return true;
			} else if (chat_layout_face.getVisibility() == View.VISIBLE) {
				chat_layout_face.setVisibility(View.GONE);
				return true;
			} else if (chat_layout_more.getVisibility() == View.VISIBLE) {
				chat_layout_more.setVisibility(View.GONE);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	//-------------------------------------------------------------------
	
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
        return sbBuffer.toString();
    }
    
    public void showLoadingProgress(Context context, int str) {
		UIUtils.getInstance()
				.showNetLoadDialog(context, context.getString(str));
	}

	/**
	 * 取消加载进度条显示
	 */

	public void cancelLoadingProgress() {
		UIUtils.getInstance().cancelNetLoadDialog();
	}

	public void setLoginStatus(boolean status) {
		MainApp.isLogin = status;
		if (!status) {
			MainApp.currentUser = null;
		}
	}
	
	public User getUserInfo() {
		return MainApp.currentUser;
	}
	
	private void startLoginActivity() {

		setLoginStatus(false);
		Intent intent = new Intent(Chat2Activity.this, LoginActivity.class);
		startActivity(intent);
		((MainApp) getApplication()).changeUser();
	}
	
	private DialogInterface.OnClickListener mTokenLisenter = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (which == DialogInterface.BUTTON_POSITIVE) {
				// // Token无效处理 当Token无效的时候，重新登录，并清除旧的token记录。
				DataUtils.getInstance(Chat2Activity.this).updateUserToken("", getUserInfo().userName);
				startLoginActivity();
				dialog.cancel();
			}
		}
	};
	
	public void showNoTokenDialog() {
		UIUtils.getInstance().showNoCancelDialog(this, "登录过期，请重新登录", mTokenLisenter);
	}
    
    public void head_xiaohei(View v) {     //
//    	Intent intent = new Intent (ChatActivity.this, FriendInfoActivity.class);			
//		startActivity(intent);
      } 
}