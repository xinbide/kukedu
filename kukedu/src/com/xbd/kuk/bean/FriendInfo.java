package com.xbd.kuk.bean;import com.xbd.kuk.model.Model;/** * 用户信息表对应 Table "FriendInfo" *  * @ClassName: OBFriendInfo * @Description: TODO * @author zhangchunzhe * @date 2013-12-11 下午6:34:18 *  */public class FriendInfo extends Model<String> {	private int mId;	private String mFriendId = "";	private String mFriendSessionID = "";	private String mFriendUserName = "";	private String mFriendUserIconUrl = "";	private String mFriendUserLocalIconUrl = "";	public int getmId() {		return mId;	}	public void setmId(int mId) {		this.mId = mId;	}	public String getmFriendId() {		return mFriendId;	}	public void setmFriendId(String mFriendId) {		this.mFriendId = mFriendId;	}	public String getmFriendSessionID() {		return mFriendSessionID;	}	public void setmFriendSessionID(String mFriendSessionID) {		this.mFriendSessionID = mFriendSessionID;	}	public String getmFriendUserName() {		return mFriendUserName;	}	public void setmFriendUserName(String mFriendUserName) {		this.mFriendUserName = mFriendUserName;	}	public String getmFriendUserIconUrl() {		return mFriendUserIconUrl;	}	public void setmFriendUserIconUrl(String mFriendUserIconUrl) {		this.mFriendUserIconUrl = mFriendUserIconUrl;	}	public String getmFriendUserLocalIconUrl() {		return mFriendUserLocalIconUrl;	}	public void setmFriendUserLocalIconUrl(String mFriendUserLocalIconUrl) {		this.mFriendUserLocalIconUrl = mFriendUserLocalIconUrl;	}}