package com.fidku.geoluks.listener;
 
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.fidku.geoluks.HomeActivity;
import com.fidku.geoluks.ProductActivity;
import com.fidku.geoluks.R;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

public class HomeSwipeListViewListener extends BaseSwipeListViewListener {

	private HomeActivity activity;
	private SwipeListView swipelistview;

	public HomeSwipeListViewListener(HomeActivity activity,
			SwipeListView swipelistview) {
		this.activity = activity;
		this.swipelistview = swipelistview;
	}

	@Override
	public void onOpened(int position, boolean toRight) { 

		if (swipelistview.getChildAt(position) != null
				&& swipelistview.getChildAt(position).findViewById(R.id.btns) != null) {
			swipelistview.getChildAt(position).findViewById(R.id.btns)
					.setVisibility(!toRight ? View.VISIBLE : View.INVISIBLE);
			swipelistview.getChildAt(position)
					.findViewById(R.id.msj_element_confirmed)
					.setVisibility(toRight ? View.VISIBLE : View.INVISIBLE);
			if (toRight) {
				activity.setCkeckPrice(position, true);
			} else {
				swipelistview.closeSwipeMenuIn(position, 3000);
			}

		}
	}

	  

	@Override
	public void onStartOpen(int position, int action, boolean right) {
		Log.d("swipe",
				String.format("onStartOpen %d - action %d", position, action));
		if (swipelistview.getChildAt(position) != null
				&& swipelistview.getChildAt(position).findViewById(R.id.btns) != null) {
			swipelistview.getChildAt(position).findViewById(R.id.btns)
					.setVisibility(View.INVISIBLE);
			swipelistview.getChildAt(position)
					.findViewById(R.id.msj_element_confirmed)
					.setVisibility(View.INVISIBLE);

		}

	}        

	@Override
	public void onClickFrontView(int position) {
		Log.d("swipe", String.format("onClickFrontView %d", position));
		Intent product = new Intent(activity, ProductActivity.class);
		product.putExtra(ProductActivity.ParamProduct, activity.getItemData()
				.get(position).getJsonString()); 
		activity.startActivity(product);
	}

	@Override
	public void onClickBackView(int position) {  
		swipelistview.closeAnimate(position); 
	}
 
}
