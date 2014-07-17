package com.xbd.kuk.bean;

import com.xbd.kuk.model.Model;

/**
 * 
 * @ClassName: FriendItem
 * @Description:
 * @author zhangchunzhe
 * @date 2013-10-31 下午1:48:09
 * 
 */
public class FriendItem extends Model<String> {
	// 好友ID
	private int mUserID;
	// 好友名称
	private String mUserName;
	// 好友图片
	private String mUserIcon;
	//
	public String sortKey;
	//
	public String firstKey = "";
	//
	public boolean isFirstIndex;

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getFirstKey() {
		return firstKey;
	}

	public void setFirstKey(String firstKey) {
		this.firstKey = firstKey;
	}

	public boolean isFirstIndex() {
		return isFirstIndex;
	}

	public void setFirstIndex(boolean isFirstIndex) {
		this.isFirstIndex = isFirstIndex;
	}

	public int getmUserID() {
		return mUserID;
	}

	public void setmUserID(int mUserID) {
		this.mUserID = mUserID;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getmUserIcon() {
		return mUserIcon;
	}

	public void setmUserIcon(String mUserIcon) {
		this.mUserIcon = mUserIcon;
	}
}
