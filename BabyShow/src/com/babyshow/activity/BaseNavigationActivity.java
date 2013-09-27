package com.babyshow.activity;

import com.babyshow.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class BaseNavigationActivity extends FragmentActivity {
	
	protected TextView mLeftTextView;
	protected TextView mRightTextView;
	protected TextView mTitleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framwork);
	}
	
}
