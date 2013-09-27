package com.babyshow.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.babyshow.application.BabyShowApplication;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * 
 * 1.异步加载给路径图片，给ID的资源图，给ApplicationInfo的程序图标</br>
 * 2.使用LruCache做了缓存</br>
 * 使用说明：用对应的接口，给路径，resID或者ApplicationInfo，并给与需要设置的ImageView在异步加载好图片后会自动设置，
 * 支持ImageView是被重用的（如ListView表项重用），可以设置默认drawable</br>
 * 使用提供的内部类FetcherPorperties设置参数，然后传入进行创建，传入null则按照默认值创建.
 * @author chivyzhuang
 *
 */
public class ImageFetcher {
	
	public static class FetcherPorperties {
		
		/** size 单位是byte</br>默认是1MB*/
		public int cacheSize = 1024 * 1024 * 1;
		
		/** 确定是否使用缓存</br>默认使用 */
		public boolean useCache = true;
		
		/** 请求加载图片按照先进先出策略，并不是所有的图片都会加载，只加载最新的requestQueueSize张</br>默认是10 */
		public int requestQueueSize = 10;
		
		/** 指定加载图片大小,加载应用程序图标此标记无效</br>默认不指定 */
		public boolean requireBoundsSize = false;
		public int requiredHeight = -1;
		public int requiredWidth = -1;
	}
	
	private SingleThreadTask mLooperThread = null;
	

	// 是否要使用缓存以及缓存占内存的使用率
	private int mCacheSize;
	private boolean mUseCache;
	private LruCache<Integer, Bitmap> mCache;
	
	// 请求队列
	private int mRequestQueueSize;
	private BlockingQueue<RequestBean> mRequestQueue;
	
	/**
	 * 传入properties创建fetcher，如果传入null则按照默认值创建,见{@link FetcherPorperties}
	 * @param properties
	 * @param context
	 */
	public ImageFetcher(FetcherPorperties properties, Context context) {
		
		if (properties == null) {
			properties = PropergetDefaultProperties();
		}
		
		// 设置属性
		mCacheSize = properties.cacheSize;
		mUseCache = properties.useCache;
		mRequestQueueSize = properties.requestQueueSize;
		
		// 如果有需要，创建缓存
		if (mUseCache) {
			mCache = new LruCache<Integer, Bitmap>(mCacheSize) {
	            @Override
	            protected int sizeOf(Integer key, Bitmap bitmap) {
	                // The cache size will be measured in kilobytes rather than
	                // number of items.
	            	return bitmap.getRowBytes() * bitmap.getHeight();
	            }
	        };
		}
		
		// 创建请求队列
		mRequestQueue = new ArrayBlockingQueue<RequestBean>(mRequestQueueSize);
		
		beginFetch();
	}
	
	/**
	 * drawableID指定图片，iv指定目标，可以设置默认图片defaultDrawahble
	 * @param drawableID
	 * @param mImageView
	 * @param defaultDrawahble
	 */
	public void fetchResourceDrawable(int drawableID, ImageView iv, Drawable defaultDrawahble) {
		// 关联ImageView与加载ID
		iv.setTag(drawableID);
		
		// 如果使用缓存且缓存中图片存在，则重用
		Bitmap bmp;
		if (mUseCache && (bmp = mCache.get(drawableID)) != null) {
			iv.setImageBitmap(bmp);
		}
		else {
			// 如果有默认图片，先设置为默认图片
			if (defaultDrawahble != null) iv.setImageDrawable(defaultDrawahble);
			
			// 加入请求队列
			RequestBean bean = new RequestBean();
			bean.mResID = drawableID;
			bean.mImageView = iv;
			bean.mType = RequestBean.TYPE_PACKAGE_DRAWABLE;
			request(bean);
		}
	}
	
	
	/**
	 * 传入appInfo设置应用程序图标
	 * @param pkgName
	 * @param iv
	 * @param defaultDrawahble
	 */
	public void fetchIcon(String pkgName, ImageView iv, Drawable defaultDrawahble) {
		// 关联ImageView与加载ID
		iv.setTag(pkgName);
		
		// 如果使用缓存且缓存中图片存在，则重用
		Bitmap bmp;
		if (mUseCache && (bmp = mCache.get(pkgName.hashCode())) != null) {
			iv.setImageBitmap(bmp);
		}
		else {
			// 如果有默认图片，先设置为默认图片
			if (defaultDrawahble != null) iv.setImageDrawable(defaultDrawahble);
			
			// 加入请求队列
			RequestBean bean = new RequestBean();
			bean.mPkgName = pkgName;
			bean.mImageView = iv;
			bean.mType = RequestBean.TYPE_APP_ICON;
			request(bean);
		}
	}
	
	/**
	 * 传入绝对路径设置图片
	 * @param path
	 * @param iv
	 * @param defaultDrawahble
	 */
	public void fetchImageDrawable(String path, ImageView iv, Drawable defaultDrawahble) {
		fetchImageDrawable(path, iv, defaultDrawahble, 0, 0);
	}
	
	/**
	 * 传入绝对路径设置图片
	 * @param path
	 * @param iv
	 * @param defaultDrawahble
	 * @param requiredWidth
	 * @param requiredHeight
	 */
	public void fetchImageDrawable(String path, ImageView iv, Drawable defaultDrawahble, int requiredWidth, int requiredHeight) {
		// 关联ImageView与加载ID
		iv.setTag(path);
		
		// 如果使用缓存且缓存中图片存在，则重用
		Bitmap bmp;
		if (mUseCache && (bmp = mCache.get(path.hashCode())) != null) {
			iv.setImageBitmap(bmp);
		}
		else {
			// 如果有默认图片，先设置为默认图片
			if (defaultDrawahble != null) iv.setImageDrawable(defaultDrawahble);
			
			// 加入请求队列
			RequestBean bean = new RequestBean();
			bean.mPath = path;
			bean.mImageView = iv;
			bean.mType = RequestBean.TYPE_RAW;
			bean.mRequireWidth = requiredWidth;
			bean.mRequireHeight = requiredHeight;
			request(bean);
		}
	}
	
	/**
	 * 关闭fetcher</br>
	 * 由于在创建的时候建立了一条线程，此处关闭。
	 */
	public void close() {
		RequestBean bean = new RequestBean();
		bean.mType = RequestBean.TYPE_EXIT;
		request(bean);
	}
	
	
	/**
	 * 开启一条线程取数据并加载bitmap
	 */
	private void beginFetch() {
		if (mLooperThread == null || mLooperThread.getThread() == null) {
			mLooperThread = new SingleThreadTask() {
				@Override
				public void task() {
					loadBitmap();
				}
			};
			mLooperThread.startTask();
		}
	}
	
	/**
	 * 开始从队列中拿到请求，并开始加载Bitmap</br>
	 * 由于有ID和路径两种加载，所以需要区分一下
	 */
	private synchronized void loadBitmap() {
		Bitmap bmp = null;
		while (true) {
			try {
				// 取队列头
				RequestBean bean = mRequestQueue.take();
				
				switch (bean.mType) {
				case RequestBean.TYPE_APP_ICON:
					bmp = loadBitmapFromAppInfo(bean);
					break;
				case RequestBean.TYPE_PACKAGE_DRAWABLE:
					bmp = loadBitmapFromID(bean.mResID);
					break;
				case RequestBean.TYPE_RAW:
					bmp = loadBitmapFromPath(bean.mPath, bean.mRequireWidth, bean.mRequireHeight);
					break;
				case RequestBean.TYPE_EXIT:
					return;
				default:
					break;
				}
				
				// 如果加载图片成功，设置ImageView
				if (bmp != null) {
					// 将结果关联进去
					bean.mBmp = bmp;
					
					// 如果有使用缓存，加入缓存
					if (mUseCache) {
						switch (bean.mType) {
						case RequestBean.TYPE_APP_ICON:
							mCache.put(bean.mPkgName.hashCode(), bmp);
							break;
						case RequestBean.TYPE_PACKAGE_DRAWABLE:
							mCache.put(bean.mResID, bmp);
							break;
						case RequestBean.TYPE_RAW:
							mCache.put(bean.mPath.hashCode(), bmp);
							break;
						default:
							break;
						}
					}
					
					// 发送消息进行设置ImageView
					// 注意判断一下有效性，是否被重用了
					Message msg = new Message();
					msg.what = BmpLoadCompletionHandler.MSG_BMP_LOAD_COMPLETION;
					msg.obj = bean;
					mHandler.sendMessage(msg);
					
					bmp = null;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param appInfo
	 * @return
	 */
	private Bitmap loadBitmapFromAppInfo(RequestBean bean) {
		// 根据AppInfo的icon和包名加载
		 Context context = BabyShowApplication.getAppContext();
		if (context != null) {
			Drawable drawable = null;
			try {
				drawable = context.getPackageManager().getApplicationIcon(bean.mPkgName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (drawable != null) {
				if (drawable instanceof BitmapDrawable) {
					return ((BitmapDrawable)drawable).getBitmap();
				} else {
					return drawableToBitmap(drawable);
				}
			}
		}
		else {
			close();
		}
		return null;
	}
	
	private Bitmap loadBitmapFromID(int resID) {
		// 根据ID加载
		Context context = BabyShowApplication.getAppContext();
		if (context != null) {
			return ((BitmapDrawable)(context.getResources().getDrawable(resID))).getBitmap();
		}
		else {
			close();
			
		}
		return null;
	}
	
	private Bitmap loadBitmapFromPath(String pathValue, int requiredWidth, int requiredHeight) {
		//construction options
		//avoid most OOM occasions
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		options.inTempStorage = new byte[16*1024];
		options.inInputShareable = true;
		options.inPurgeable = true;
		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(pathValue);
			
			if (requiredHeight != 0 && requiredWidth != 0) {
				// get pic rectangle size
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(stream.getFD(), null, options);
				
				// resize
				options.inJustDecodeBounds = false;
				int size;
				if(options.outWidth <= requiredWidth && options.outHeight <= requiredHeight){
					size = 1;
				}
				else{
					int width = options.outWidth;
					int height = options.outHeight;
					final int heightRatio = Math.round((float) height / (float) requiredHeight);
		            final int widthRatio = Math.round((float) width / (float) requiredWidth);
		            size = heightRatio < widthRatio ? heightRatio : widthRatio;
		            final float totalPixels = width * height;
	
		            // Anything more than 2x the requested pixels we'll sample down further
		            final float totalReqPixelsCap = requiredWidth * requiredHeight * 2;
	
		            while (totalPixels / (size * size) > totalReqPixelsCap) {
		            	size++;
		            }
				}
				options.inSampleSize = size;
			}
			
			
			return BitmapFactory.decodeFileDescriptor(stream.getFD(), null, options);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public void request(RequestBean bean) {
		beginFetch();
		try {
			if (mRequestQueue.size() == mRequestQueueSize) {
				mRequestQueue.poll();
			}

			mRequestQueue.put(bean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private final BmpLoadCompletionHandler mHandler = new BmpLoadCompletionHandler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_BMP_LOAD_COMPLETION: {
				RequestBean bean = (RequestBean)msg.obj;
				if (isValid(bean)) {
					bean.mImageView.setImageBitmap(bean.mBmp);
				}
			}
				break;
			default:
				break;
			}
		}
		
	};
	
	private boolean isValid(RequestBean bean) {
		switch (bean.mType) {
		case RequestBean.TYPE_APP_ICON:
			return ((String)(bean.mImageView.getTag())).equals(bean.mPkgName);
		case RequestBean.TYPE_PACKAGE_DRAWABLE:
			return (Integer)(bean.mImageView.getTag()) == bean.mResID;
		case RequestBean.TYPE_RAW:
			return ((String)(bean.mImageView.getTag())).equals(bean.mPath);
		default:
			break;
		}
		return false;
	}
	
	private static class BmpLoadCompletionHandler extends Handler {
		public final static int MSG_BMP_LOAD_COMPLETION = 0;
	}
	
	private static class RequestBean {
		public static final int TYPE_APP_ICON = 0;
		public static final int TYPE_PACKAGE_DRAWABLE = 1;
		public static final int TYPE_RAW = 2;
		public static final int TYPE_EXIT = -1;
		
		String mPath;
		Integer mResID;
		String mPkgName;
		ImageView mImageView;
		Bitmap mBmp;
		int mType;
		int mRequireWidth;
		int mRequireHeight;
	}
	
	
	private FetcherPorperties PropergetDefaultProperties() {
		return new FetcherPorperties();
	}

	
	/**
	 * 将drawable转化成btimap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {  
        // 取 drawable 的长宽  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // 取 drawable 的颜色格式  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // 把 drawable 内容画到画布中  
        drawable.draw(canvas);  
        return bitmap;  
    }
}
