package com.xbd.kuk.app;



import com.xbd.kuk.MainActivity;
import com.xbd.kuk.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class LoadingActivity extends Activity{

	SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.layout_loading);
			
	new Handler().postDelayed(new Runnable(){
		@Override
		public void run(){
//			Intent intent = new Intent (LoadingActivity.this, WelcomeActivity.class);
//			startActivity(intent);			
			
			//读取SharedPreferences中需要的数据
	        preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
	        int count = preferences.getInt("count", 0);
	        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
	        if (count == 0) {
	            Intent intent = new Intent();
	            intent.setClass(getApplicationContext(), GuideActivity.class);
	            startActivity(intent);
	            finish();
	        }else {
				Intent intent = new Intent (LoadingActivity.this, MainActivity.class);
				startActivity(intent);
				LoadingActivity.this.finish();
	        }

	        Editor editor = preferences.edit();
	        //存入数据
	        editor.putInt("count", ++count);
	        //提交修改
	        editor.commit();
			
			LoadingActivity.this.finish();
	        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
		}
	}, 200);
   }
}