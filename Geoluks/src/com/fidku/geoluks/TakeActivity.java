package com.fidku.geoluks;

import com.fidku.geoluks.api.ApiConnection;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.Result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

public class TakeActivity extends CaptureActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_client);
	}
	
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		String codigo= rawResult.getText();
		
		
	 
		
		Intent i = new Intent(this, FormProductActivity.class);
		i.putExtra("codigo", codigo);
		startActivity(i);;  
	}
}
