package com.babyshow.activity;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.babyshow.R;
import com.babyshow.fragment.HomeFragment;
import com.babyshow.fragment.PersonalPageFragment;
import com.babyshow.fragment.PhotosFragment;

public class MainActivity extends SherlockFragmentActivity {
	
	private ViewPager mViewPager;
	private FunctionPagerAdapter mPagerAdapter;
	private ActionBar mActionBar;
	private List<Fragment> mFragmentList;
	private String[] mTabTitleArray;
	private ActionBar.TabListener mTabListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framwork);
		
		// 初始化
		prepareData();
		initUIComponents();
	}
	
	/**
	 * 准备数据，根据ID获取一些view的引用
	 */
	private void prepareData() {
		// tab的各个标题
		mTabTitleArray = getResources().getStringArray(R.array.tab_titles);
		
		// tab页面
		mFragmentList = new ArrayList<Fragment>(mTabTitleArray.length);
		mFragmentList.add(new HomeFragment());
		mFragmentList.add(new PhotosFragment());
		mFragmentList.add(new PersonalPageFragment());
		
		// 获得view引用
		mViewPager = (ViewPager)findViewById(R.id.pager);
		
		// adapter
		mPagerAdapter = new FunctionPagerAdapter(getSupportFragmentManager());
		
		// 获得Actionbar
		mActionBar = getSupportActionBar();
		
		// tab的listener
		mTabListener = new ActionBar.TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
				
			}			
		};
	}
	
	/**
	 * 对界面元素进行初始化
	 */
	private void initUIComponents() {
		// Action相关的初始化
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for(int i = 0; i < mTabTitleArray.length; i++) {
			mActionBar.addTab(mActionBar.newTab().setText(mTabTitleArray[i]).setTabListener(mTabListener));
		}
		
		
		// 页面元素
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				mActionBar.setSelectedNavigationItem(pos);
			}
		});
	}
	
	private class FunctionPagerAdapter extends FragmentStatePagerAdapter {

		public FunctionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return mFragmentList.get(pos);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}
		
	}

}
