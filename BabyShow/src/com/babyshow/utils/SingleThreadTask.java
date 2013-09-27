package com.babyshow.utils;

import java.util.List;
/**
 * 封装的简易单线程任务类
 * @author aha
 *
 */
public abstract class SingleThreadTask {
	
	private static final String TAG = SingleThreadTask.class.getSimpleName();
	
	// 当前的唯一任务
	private volatile Thread mThread;
	
	// 互斥锁
	private volatile Object THREAD_LOCK = new Object();
	
	// 参数表
	protected volatile List<Object> mParamsList;
	
	// 线程名
	private volatile String mThreadName;
	
	public SingleThreadTask() {
		this(TAG, null);
	}
	
	public SingleThreadTask(String name) {
		this(name, null);
	}
	
	public SingleThreadTask(String name, List<Object> params) {
		// 防止null
		if (name == null || name.equals(""))  name = TAG;
		
		mThreadName = name;
		mParamsList = params;
	}
	
	/**
	 * 开始一个任务
	 * @return	如果是新建一个线程返回true，重用返回false
	 */
	public boolean startTask() {
		synchronized (THREAD_LOCK) {
			boolean isNewThread = false;
			
			if (mThread == null) {
				mThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							task();
						} catch (Exception e) {
							e.printStackTrace();
							mThread = null;
						}
					}
				});
				
				mThread.setDaemon(true);
				mThread.setName(mThreadName);
				mThread.setPriority(Thread.NORM_PRIORITY);
				
				mThread.start();
			}
			else {
				mThread.start();
			}
			
			return isNewThread;
		}
	}
	
	/**
	 * 停止当前任务
	 */
	public void stopTask() {
		synchronized (THREAD_LOCK) {
			Thread thread = mThread;
			mThread = null;
			thread.interrupt();
		}
	}
	
	public Thread getThread() {
		return mThread;
	}
	
	/**
	 * 必须实现的方法，具体执行任务
	 * @param params
	 * @throws Exception
	 */
	protected abstract void task() throws Exception;
}
