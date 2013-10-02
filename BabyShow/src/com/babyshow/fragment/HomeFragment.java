package com.babyshow.fragment;

import com.babyshow.R;
import com.babyshow.utils.IconCreator;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeFragment extends Fragment{
	
	private ImageView mProfileIcon;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_home, container, false);
		mProfileIcon = (ImageView) layout.findViewById(R.id.profile_icon_iv);
		Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			IconCreator ic = new IconCreator(bd.getBitmap());
			ic.createIcon();
			mProfileIcon.setBackgroundDrawable(new BitmapDrawable(ic.getCreatedIcon()));
		}
		
		return layout;
	}
}
