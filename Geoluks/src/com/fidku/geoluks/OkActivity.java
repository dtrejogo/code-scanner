package com.fidku.geoluks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class OkActivity extends Activity {

	public static final String MSJ_OK = "OK_VIEW_MSJ_OK";
	private TextView tv_msj;
	private ImageButton btn_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.ok_view);
		tv_msj=(TextView)findViewById(R.id.ok_view_msj);
		if(getIntent().getStringExtra(OkActivity.MSJ_OK)!=null){
			tv_msj.setText(getIntent().getStringExtra(OkActivity.MSJ_OK));
		}
		
		btn_ok=(ImageButton)findViewById(R.id.btn_ok_view);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OkActivity.this, HomeActivity.class));
				
			}
		});
	}
}
