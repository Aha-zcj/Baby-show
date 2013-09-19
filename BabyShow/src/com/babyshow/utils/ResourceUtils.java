package com.babyshow.utils;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import com.babyshow.application.BabyShowApplication;

public class ResourceUtils {

	@SuppressWarnings("unused")
	private static final String TAG = ResourceUtils.class.getSimpleName();

	public static String getString(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getString(resId);
	}

	public static Drawable getDrawable(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getDrawable(resId);
	}

	public static XmlResourceParser getAnimation(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getAnimation(resId);
	}

	public static Integer getInteger(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getInteger(resId);
	}

	public static String[] getStringArray(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getStringArray(resId);
	}

	public static Boolean getBoolean(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getBoolean(resId);
	}

	public static XmlResourceParser getLayout(int resId) {
		return BabyShowApplication.getAppContext().getResources()
				.getLayout(resId);
	}

	public static LayoutInflater getLayoutInflator() {
		return LayoutInflater.from(BabyShowApplication.getAppContext());
	}
	
}
