package com.babyshow.application;

import android.app.Application;
import android.content.Context;

public class BabyShowApplication extends Application{

	private static Context mAppContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 放在最前面
		mAppContext = getApplicationContext();
		
		
	}
	
	public static Context getAppContext() {
		return mAppContext;
	}
	
}
