package com.babyshow.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babyshow.R;

public class PersonalPageFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_personal, container, false);
	}

}
