package com.xbd.kuk.app;

import com.xbd.kuk.MainActivity;
import com.xbd.kuk.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginStartActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login_start);
	}
    public void start_login(View v) {  
      	Intent intent = new Intent();
		intent.setClass(LoginStartActivity.this, LoginActivity.class);
		startActivity(intent);
		//this.finish();
    }
    public void start_register(View v) {  
      	Intent intent = new Intent();
		intent.setClass(LoginStartActivity.this, MainActivity.class);
		startActivity(intent);
		//this.finish();
    }
	
}
