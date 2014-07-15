package com.xbd.kuk.app;

import com.xbd.kuk.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

public class LauncherActivity extends Activity {

	Intent intent;
//	SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_launcher);

    	new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){

				//读取SharedPreferences中需要的数据
//		        preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
//		        int count = preferences.getInt("count", 0);
//		        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
//		        if (count == 0) {
//		            Intent intent = new Intent();
//		            intent.setClass(getApplicationContext(), GuideActivity.class);
//		            startActivity(intent);
//		            finish();
//		        }else {
					Intent intent = new Intent (LauncherActivity.this, LoginStartActivity.class);			
					startActivity(intent);			
					LauncherActivity.this.finish();
//		        }
//
//		        Editor editor = preferences.edit();
//		        //存入数据
//		        editor.putInt("count", ++count);
//		        //提交修改
//		        editor.commit();
			}
		}, 1000);
		
		intent = new Intent();
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		intent.setAction("BIZSHORTCUT");
		intent.putExtra("target", "file");
//	    startActivityForResult(intent, MENU_ACTION_FILE);
	}
}

