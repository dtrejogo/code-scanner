package com.fidku.geoluks;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeListFragment extends Fragment {
	
	private View rootView;
	private Activity context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.fragment_home_list,
				container, false);

		this.context = getActivity();

		return this.rootView;
	}

}
