package com.babyshow.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babyshow.R;
import com.babyshow.entity.Growth;

public class HomeFragment extends Fragment{
	
	private List<Growth> mGrowthList;
	private ListView mListView;
	private GrowthAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_home, container, false);
		mGrowthList = new ArrayList<Growth>();
		mAdapter = new GrowthAdapter(getActivity());
		mListView = (ListView) layout.findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		
		return layout;
	}
	
	class GrowthAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		public GrowthAdapter(Context ctx) {
			mInflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return mGrowthList.size();
		}

		@Override
		public Object getItem(int position) {
			return mGrowthList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.home_talk_list_item, parent, false);
				holder = new ViewHolder();
				holder.ivProfileIcon = (ImageView) convertView.findViewById(R.id.profile_icon_iv);
				holder.tvBabyName = (TextView) convertView.findViewById(R.id.baby_name_tv);
				holder.tvGrowthContent = (TextView) convertView.findViewById(R.id.growth_content_tv);
				holder.btnComment = (Button) convertView.findViewById(R.id.comment_btn);
				holder.btnLike = (Button) convertView.findViewById(R.id.like_btn);
				
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			//更新操作
			
			
			
			
			
			return convertView;
		}
		
		private class ViewHolder {
			public ImageView ivProfileIcon;
			public TextView tvBabyName;
			public TextView tvGrowthContent;
			public TextView tvGrowthTime;
			public Button btnComment;
			public Button btnLike;
		}
	}
}
