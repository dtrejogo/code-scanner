package com.fidku.geoluks;

import android.os.Bundle;

public class AddListActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_list);
		super.setTitle(getString(R.string.list_home_title));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new AddListFragment()).commit();
		}
	}

	

}
