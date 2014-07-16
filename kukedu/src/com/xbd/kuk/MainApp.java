package com.xbd.kuk;

import java.util.LinkedList;
import java.util.List;

import com.xbd.kuk.datastart.DataUtils;
import com.xbd.kuk.model.User;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @ClassName: MainApp
 * @Description: 当前应用程序APP
 * @author zhangchunzhe
 * @date 2013-1-14 下午2:33:26
 * 
 */
public class MainApp extends Application {

	/**
	 * 用户是否登录当前程序
	 * 
	 */
	public static boolean isLogin = false;
	/**
	 * 当前用户的帐户信息
	 */

	public static User currentUser = null;

	private List<Activity> mList = new LinkedList<Activity>();

//	private SoftReference mObSoftRefrences = new SoftReference();
//
//	public SoftReference getmObSoftRefrences() {
//		return mObSoftRefrences;
//	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() { // 遍历List，退出每一个Activity
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
			mList.clear();
//			mObSoftRefrences.clearSoftReferenceBitmap();
//			OBFileUtil.getInstance(getBaseContext()).clearAllDownloadTempFile();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
//			MobclickAgent.onKillProcess(this);
		}
	}

	/**
	 * 
	 */
	public void changeUser() { // 遍历List，退出每一个Activity
		try {
			for (Activity activity : mList) {
				if (activity != null) {
					activity.finish();
					activity = null;
				}
			}
			mList.clear();
//			mObSoftRefrences.clearSoftReferenceBitmap();
//			OBFileUtil.getInstance(getBaseContext()).clearAllDownloadTempFile();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc(); // 告诉系统回收

	}

	@Override
	public void onCreate() {
		super.onCreate();

		
		if (currentUser == null) {
			User obBarUser = DataUtils.getInstance(this).getLoginUserInfo();
			if (obBarUser != null) {
				currentUser = obBarUser;
			}
		}

		// JPushInterface.setDebugMode(true);
		// JPushInterface.init(this);

	}
}
