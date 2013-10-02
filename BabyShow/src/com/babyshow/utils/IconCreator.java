package com.babyshow.utils;

import com.babyshow.application.BabyShowApplication;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 通过传入的drawable，制造圆形图标
 * @author chivyzhuang
 *
 */
public class IconCreator {
	
	// 原始bmp或者完成后的bmp
	private Bitmap mBitmap;
	
	// 透明色
	private int mTransparent;
	
	// 确定是否制作
	private boolean hasMade;
	
	public IconCreator(Bitmap bmp) {
		mBitmap = bmp;
		hasMade = false;
		mTransparent = BabyShowApplication.getAppContext().getResources().getColor(android.R.color.transparent);
	}
	
	/**
	 * 耗时与bmp的大小成正比，所以最好在异步线程做，在获取创建的图标之前要调用这个函数<br>
	 * @return
	 */
	public boolean createIcon() {
		Rect srcRect = null;
		Rect destRect = null;
		
		// 判断是否正方形，如果不是，以矩形中心为中心，创建最大适应的正方形
		int height = mBitmap.getHeight();
		int width = mBitmap.getWidth();
		// 这里对长方形的bmp做优化
		if (height == width) {
			// 正方形
			srcRect = destRect = new Rect(0, 0, width - 1, height - 1);
		} else if (height < width) {
			// 横向长方形
			srcRect = new Rect((width - height) / 2, 0, (width + height) / 2, height - 1);
			destRect = new Rect(0, 0, height - 1, height - 1);
		} else {
			// 纵向长方形
			srcRect = new Rect(0, (height - width) / 2, width - 1, (width + height) / 2);
			destRect = new Rect(0, 0, width - 1, width - 1);
		}
		
		// 绘制
		Bitmap output = Bitmap.createBitmap(destRect.width(), destRect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output); 
		Paint paint = new Paint(); 
		paint.setAntiAlias(true); 
		canvas.drawColor(mTransparent);
		paint.setColor(Color.BLACK); 
		canvas.drawRoundRect(new RectF(destRect), (float)destRect.width() / 2, (float)destRect.width() / 2, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(mBitmap, srcRect, destRect, paint); 
		mBitmap = output;
		
		hasMade = true;
		return hasMade;
	}
	
	/**
	 * 返回制作的icon
	 * @return	成功返回icon，如果失败或者还没有调用创建的函数则返回null
	 */
	public Bitmap getCreatedIcon() {
		return hasMade ? mBitmap : null;
	}
}
