package com.babyshow.utils;



import java.util.concurrent.atomic.AtomicBoolean;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public abstract class CommonThreadTask<Params, Progress, Result>{

	private static final String TAG = "CommonThreadTask";
	
	private Thread mThread;
	
	/**
     * Indicates the current status of the task. Each status will be set only once
     * during the lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncTask#onPostExecute} has finished.
         */
        FINISHED,
    }
    
    private Status mStatus = Status.PENDING;
    
    private AtomicBoolean mCancelled = new AtomicBoolean(false);
    
    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
	
	private InternalHandler mHandler = new InternalHandler();
	
	private WorkRunnable<Params> mWork;
	
	public CommonThreadTask() {
		mWork =  new WorkRunnable<Params>() {

			@Override
			public void run() {
				
				postResult(doInBackground(mParams));
			}
			
		};
		
		mThread = new Thread(mWork);
	}
	
	/**
	 * post the result to the handler
	 * @param result
	 */
	protected void postResult(Result result) {
		
		Message msg = mHandler.obtainMessage(MESSAGE_POST_RESULT, result);
		msg.sendToTarget();
		
	}

	public CommonThreadTask<Params, Progress, Result> execute(Params...params ) {
		 if (mStatus != Status.PENDING) {
	            switch (mStatus) {
	                case RUNNING:
	                    throw new IllegalStateException("Cannot execute task:"
	                            + " the task is already running.");
	                case FINISHED:
	                    throw new IllegalStateException("Cannot execute task:"
	                            + " the task has already been executed "
	                            + "(a task can be executed only once)");
	            }
	        }

	        mStatus = Status.RUNNING;

	        onPreExecute();
	        mWork.mParams = params;
	        mThread.start();
	        
	        return this;
	}
	
	public Status getStatus() {
		return mStatus;
	}
	
	/**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     *
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     *
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param result The result of the operation computed by {@link #doInBackground}.
     *
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    protected void onPostExecute(Result result) {
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     *
     * @see #publishProgress
     * @see #doInBackground
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * <p>The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.</p>
     *
     * @param result The result, if any, computed in
     *               {@link #doInBackground(Object[])}, can be null
     *
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     *
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }
    /**
     * cancel the thread
     * @return if thread's status is running ,cancel and return true, else return false
     */
    public boolean cancel() {
    	if(!isCancelled() && mStatus == Status.RUNNING) {
    		mCancelled.set(true);
        	mThread.interrupt();
        	return true;
    	}
    	
    	return false;
    }
    
    public boolean isCancelled() {
    	return mCancelled.get();
    }
    
    /**
     * This method can be invoked from {@link #doInBackground} to
     * publish updates on the UI thread while the background computation is
     * still running. Each call to this method will trigger the execution of
     * {@link #onProgressUpdate} on the UI thread.
     *
     * {@link #onProgressUpdate} will note be called if the task has been
     * canceled.
     *
     * @param values The progress values to update the UI with.
     *
     * @see #onProgressUpdate
     * @see #doInBackground
     */
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            mHandler.obtainMessage(MESSAGE_POST_PROGRESS,
                    values).sendToTarget();
        }
    }
    /**
     * 在{{@link #doInBackground} 之后执行， 
     * 若任务已被取消，则调用 {@link #onCancelled},否则调用{@link #onPostExecute}
     * @param result
     */
    public void finish(Result result) {
		if(isCancelled())
			onCancelled(result);
		else
			onPostExecute(result);
		mStatus = Status.FINISHED;
	}

	private class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			case MESSAGE_POST_RESULT:
				Result result = (Result)msg.obj;
				finish(result);
				break;
			case MESSAGE_POST_PROGRESS:
				Progress[] value = (Progress[])msg.obj;
				onProgressUpdate(value);
				break;
			}
		}
	}
	
	private abstract class WorkRunnable<Params> implements Runnable {
		public Params[] mParams;
	}

	
}
