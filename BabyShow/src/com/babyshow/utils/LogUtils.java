package com.babyshow.utils;

import java.util.Locale;

import android.util.Log;

public class LogUtils {
	
	@SuppressWarnings("unused")
	private static final String TAG = LogUtils.class.getSimpleName();
	
	// 类名-方法名-文件名:行号
	private final static String DEFAULT_MSG_FORMAT = "%s  %s:%d";
	
	
	/**
	 * 输出debug级log
	 * @param tag 如果为null，则tag为默认值，即调用的方法所在类的类名
	 * @param msg 如果为null，则msg为默认值，即调用的方法名，文件名以及行数
	 */
	public static void debug(String tag, String msg) {
		// 保证调用安全
		if (tag == null) tag = getDefaultTag();
		if (msg == null) msg = getDefaultMsg();
		
		Log.d(tag, msg);
	}
	
	
	
	/**
	 * debug级log输出，tag是调用的方法所在的类的类名，msg是方法名-文件名:行号
	 */
	public static void debug() {
		debug(getDefaultTag(), getDefaultMsg());
	}
	
	
	/**
	 * 获得默认的msg，即"方法名  文件名:行号"
	 */
	private static String getDefaultMsg() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
		return String.format(Locale.US, DEFAULT_MSG_FORMAT, ste.getMethodName(), ste.getFileName(), ste.getLineNumber());
	}
	
	
	/**
	 * 获得默认tag，即调用的方法所在的类的类名
	 * @return
	 */
	private static String getDefaultTag() {
		String fullClassName = Thread.currentThread().getStackTrace()[4].getClassName();
		return fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
	}

}
