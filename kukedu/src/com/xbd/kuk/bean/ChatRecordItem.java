package com.xbd.kuk.bean;

import com.xbd.kuk.model.Model;

import android.net.Uri;

public class ChatRecordItem extends Model<String> {
	private static final String TAG = ChatRecordItem.class.getSimpleName();
	public int chatId;
	private int personId;
	private Uri personAvatar;
	private String personAvatarUrl;
	private String personName;
	public String chatTimeAgo;
	private String chatContent;
	
	public int getUserId(){
		return personId;
	}
	public void setUserId(int id){
		this.personId = id;
	}
	
	public String getName() {
        return personName;
    }
    public void setName(String name) {
        this.personName = name;
    }
    
	public String getContent() {
        return chatContent;
    }
    public void setConten(String content) {
        this.chatContent = content;
    }
    
    public Uri getAvatar(){
    	return personAvatar;
    }
    public void setAvatar(Uri avatar){
    	this.personAvatar = avatar;
    }
    public String getAvatarUrl(){
    	return personAvatarUrl;
    }
    public void setAvatarUrl(String avatar){
    	this.personAvatarUrl = avatar;
    }
    
	public String getTimeAgo() {
        return chatTimeAgo;
    }
    public void setTimeAgo(String timeAgo) {
        this.chatTimeAgo = timeAgo;
    }
}
