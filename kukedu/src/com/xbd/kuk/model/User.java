package com.xbd.kuk.model;

import java.util.ArrayList;

public class User extends Model<String> {

	public String userName;// 用户名
	public String password;// 密码
	public String userId;// 用户OES平台ID
	public String userBaseID;// 学吧后台ID,用于即时通讯
	public String userRoleType;// 教师 ，学生
	public String faceUrl;
	public String nickname;
	public String token;

	public ArrayList<UserProfessionItem> mProfession; // 专业信息

	public UserProfessionItem getmProfession() {
		if (mProfession != null && mProfession.size() > 0) {
			return mProfession.get(0);
		}
		return null;
	}

	public void setProfession(UserProfessionItem item) {
		mProfession = new ArrayList<UserProfessionItem>();
		mProfession.add(item);
	}
}