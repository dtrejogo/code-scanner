package com.fidku.geoluks.listener;

import com.fidku.geoluks.AddListActivity;
import com.fidku.geoluks.CompareActivity;
import com.fidku.geoluks.HomeActivity;
import com.fidku.geoluks.HomeOfferActivity;
import com.fidku.geoluks.ListActivity;
import com.fidku.geoluks.ProfileActivity;
import com.fidku.geoluks.R;
import com.fidku.geoluks.TakeActivity;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class HomeListener implements OnClickListener, OnTouchListener {

	private HomeActivity view;

	public HomeListener(HomeActivity homeActivity) {
		view = homeActivity;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
	 

		ImageButton b = (ImageButton) arg0;
		
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) b.getLayoutParams();
		params.bottomMargin = 0;
		switch (arg1.getAction()) { 
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
			params.bottomMargin = 15;
			break; 
		} 

		return false;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_offer:
			view.startActivity(new Intent(view, HomeOfferActivity.class));
			break;
		case R.id.btn_compare:
			view.startActivity(new Intent(view, CompareActivity.class));
			break;
		case R.id.btn_take:
			view.startActivity(new Intent(view, TakeActivity.class));
			break;
		case R.id.btn_list:
			view.startActivity(new Intent(view, AddListActivity.class));
			break;

		case R.id.btn_profile:
			view.startActivity(new Intent(view, ProfileActivity.class));
			break;

		default:
			break;
		}

	}

}
