package com.xbd.kuk.app;

import com.xbd.kuk.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class FriendInfoActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_info);              
    }

	public void btn_back(View v) {     //标题栏 返回按钮
      	this.finish();
    } 
	public void btn_back_send(View v) {     //标题栏 返回按钮
     	this.finish();
    } 
	public void head_xiaohei(View v) {     //头像按钮
	   Intent intent = new Intent();
		intent.setClass(FriendInfoActivity.this,FriendInfoHead.class);
		startActivity(intent);
    } 
    
}
