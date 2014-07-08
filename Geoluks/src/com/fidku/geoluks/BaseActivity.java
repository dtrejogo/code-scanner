package com.fidku.geoluks;

import com.fidku.geoluks.utils.TypefaceSpan;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;

public abstract class BaseActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void setTitle(String title) {
		
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "CODE Light.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(s);

	}
	public void setDisplayHomeEnable(boolean value){
		getActionBar().setDisplayHomeAsUpEnabled(value);
	}
	
	
	
	

}
