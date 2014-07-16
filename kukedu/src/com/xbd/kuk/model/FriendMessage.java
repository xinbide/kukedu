package com.xbd.kuk.model;

public class FriendMessage extends Model<String> {

	private int mMegId;// 消息的ID
	private String mMsgSessionID;//区分会话id，为 login user id + mMsgAhthorID
	private String mMsgAhthorID;// 消息发送人ID；
	private String mMsgAcceptID;// 消息接收人ID；
	private String mMsgAhthorName; // 消息发送人名称
	private String mMsgAhthorIcon; // 消息发送人图像
	private String mMsgContent; // 文字消息类容
	private String mMsgAudioUrl;// 音频消息地址
	private int mMsgVideoDuration;// 音频消息时长
	private String mMsgVideoUrl;// 音频消息地址
	private String mMsgDate; // 消息发送时间
	private String mMsgStatus;// 消息状态  ： 1 未读  2已读
	private String mMsgSendStatus;//消息的发送状态 1发送中2发送成功3发送失败
	public int mIndex;// 临时添加
	private  String fromStatus; //消息来源：1自己  2好友
	private String  receiveServerTime;//消息到达的服务器时间
	private String  receiveLocalTime;//消息在本地的时间
	private String mMsgImgUrl; // 该地址用来储存图片的大图地址
	private String mMsgImgPath; // 该地址用来储存图片的小图地址
	
	private boolean isSelected;//在列表页面是否被选中



	/*消息类型
	 *  1（系统相关）
	    2（私信相关）
	    3（发言相关）
	    4（评论相关）
	    5（好友相关）
	    6（用户相关）
	 */
	private String mMsgType;

	/*
	  消息编码
	     （系统相关）1  系统通知 2系统消息
	     （私信相关）20 文本 21 语音 22 视频 23图片
	     （发言相关）40 待审核发言（转发） 41 屏蔽发言（转发）
	     （评论相关）60 待审核评论（回复） 61 屏蔽评论（回复）
	     （好友相关）80 接收到好友申请  81 好友验证通过 82 好友验证不通过
	     （用户相关）90 该用户设置了屏蔽规则  91 该用户取消了屏蔽规则 92 该用户被删除 93 该用户被禁用
	 */
	private String mMsgCode;
	


	public String getmMsgImgPath() {
		return mMsgImgPath;
	}

	public void setmMsgImgPath(String mMsgImgPath) {
		this.mMsgImgPath = mMsgImgPath;
	}
	public void setmMsgVideoUrl(String mMsgVideoUrl) {
		this.mMsgVideoUrl = mMsgVideoUrl;
	}
	
	public String getmMsgType() {
		return mMsgType;
	}

	public String getmMsgAudioUrl() {
		return mMsgAudioUrl;
	}

	public void setmMsgAudioUrl(String mMsgAudioUrl) {
		this.mMsgAudioUrl = mMsgAudioUrl;
	}
	/**
	        消息编码
	     （系统相关）1  系统通知 2系统消息
	     （私信相关）20 文本 21 语音 22 视频 23图片
	     （发言相关）40 待审核发言（转发） 41 屏蔽发言（转发）
	     （评论相关）60 待审核评论（回复） 61 屏蔽评论（回复）
	     （好友相关）80 接收到好友申请  81 好友验证通过 82 好友验证不通过
	     （用户相关）90 该用户设置了屏蔽规则  91 该用户取消了屏蔽规则 92 该用户被删除 93 该用户被禁用
	 */
	public String getmMsgCode() {
		return mMsgCode;
	}

	public void setmMsgCode(String mMsgCode) {
		this.mMsgCode = mMsgCode;
	}

	public void setmMsgType(String mMsgType) {
		this.mMsgType = mMsgType;
	}
	public void setmMsgContent(String mMsgContent) {
		this.mMsgContent = mMsgContent;
	}
	public int getmMegId() {
		return mMegId;
	}

	public void setmMegId(int mMegId) {
		this.mMegId = mMegId;
	}

	public String getmMsgAhthorID() {
		return mMsgAhthorID;
	}

	public void setmMsgAcceptID(String mMsgAcceptID) {
		this.mMsgAcceptID = mMsgAcceptID;
	}

	public String getmMsgAcceptID() {
		return mMsgAcceptID;
	}

	public void setmMsgAhthorID(String mMsgAhthorID) {
		this.mMsgAhthorID = mMsgAhthorID;
	}

	public String getmMsgAhthorName() {
		return mMsgAhthorName;
	}

	public void setmMsgAhthorName(String mMsgAhthorName) {
		this.mMsgAhthorName = mMsgAhthorName;
	}

	public String getmMsgAhthorIcon() {
		return mMsgAhthorIcon;
	}

	public void setmMsgAhthorIcon(String mMsgAhthorIcon) {
		this.mMsgAhthorIcon = mMsgAhthorIcon;
	}

	public String getmMsgContent() {
		return mMsgContent;
	}



	public String getmMsgVideoUrl() {
		return mMsgAudioUrl;
	}


	public String getmMsgImgUrl() {
		return mMsgImgUrl;
	}

	public void setmMsgImgUrl(String mMsgImgUrl) {
		this.mMsgImgUrl = mMsgImgUrl;
	}




	public String getmMsgDate() {
		return mMsgDate;
	}

	public void setmMsgDate(String mMsgDate) {
		this.mMsgDate = mMsgDate;
	}

	public String getmMsgStatus() {
		return mMsgStatus;
	}

	public void setmMsgStatus(String mMsgStatus) {
		this.mMsgStatus = mMsgStatus;
	}

	public int getmMsgVideoDuration() {
		return mMsgVideoDuration;
	}

	public void setmMsgVideoDuration(int mMsgVideoDuration) {
		this.mMsgVideoDuration = mMsgVideoDuration;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveServerTime = receiveTime;
	}

	public String getReceiveTime() {
		return receiveServerTime;
	}

	public void setmMsgSessionID(String mMsgSessionID) {
		this.mMsgSessionID = mMsgSessionID;
	}

	public String getmMsgSessionID() {
		return mMsgSessionID;
	}

	public void setReceiveLocalTime(String receiveLocalTime) {
		this.receiveLocalTime = receiveLocalTime;
	}

	public String getReceiveLocalTime() {
		return receiveLocalTime;
	}

	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public String getmMsgSendStatus() {
		return mMsgSendStatus;
	}

	public void setmMsgSendStatus(String mMsgSendStatus) {
		this.mMsgSendStatus = mMsgSendStatus;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}
	

}
