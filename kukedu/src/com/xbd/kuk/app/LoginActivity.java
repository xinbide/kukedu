package com.xbd.kuk.app;

import com.xbd.kuk.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
	}
	
	public void login_mainweixin(View v) {
    	if("test".equals(mUser.getText().toString()) && "123".equals(mPassword.getText().toString()))   //判断 帐号和密码
        {
             Intent intent = new Intent();
             intent.setClass(LoginActivity.this, LoadingActivity.class);
             startActivity(intent);
        }
        else if("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //判断 帐号和密码
        {
        	new AlertDialog.Builder(LoginActivity.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录错误")
			.setMessage("帐号或者密码不能为空，\n请输入后再登录！")
			.create().show();
        }
        else{
           
        	new AlertDialog.Builder(LoginActivity.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录失败")
			.setMessage("帐号或者密码不正确，\n请检查后重新输入！")
			.create().show();
        }
    	
    	//登录按钮
    	/*
      	Intent intent = new Intent();
		intent.setClass(Login.this,Whatsnew.class);
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
		this.finish();*/
      }  
    public void login_back(View v) {     //标题栏 返回按钮
      	this.finish();
    }  
    public void login_pw(View v) {     //忘记密码按钮
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
    } 
    
}
