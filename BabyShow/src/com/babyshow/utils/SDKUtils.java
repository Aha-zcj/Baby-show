package com.babyshow.utils;

import android.os.Build;

/**
 * SDK工具
 * @author chivyzhuang
 *
 */
public class SDKUtils {
	
	@SuppressWarnings("unused")
	private static final String TAG = SDKUtils.class.getSimpleName();
	
	// 下面是各个sdk版本
	public static final int OS_1_6 = 4;
	
	public static final int OS_2_1 = 7;
	
	public static final int OS_2_2 = 8;
	
	public static final int OS_2_3 = 10;
	
	public static final int OS_3_0 = 11;
	
	public static final int OS_3_1 = 12;
	
	public static final int OS_3_2 = 13;
	
	public static final int OS_4_0 = 14;
	
	public static final int OS_4_0_3 = 15;
	
	public static final int OS_4_1 = 16;
	
	public static final int OS_4_2 = 17;
	
	/**
	 * 获得当前所在的SDK版本号
	 * @return
	 */
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}
	
}
