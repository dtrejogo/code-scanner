package com.fidku.geoluks;

import java.io.ByteArrayOutputStream;

import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.GPSTracker;
import com.fidku.geoluks.utils.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.transition.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.provider.MediaStore;

public class AddOfferImageActivity extends BaseActivity {

	private ImageView image;
	private Button nextButton;
	private Bitmap bitmap;
	private SharedPreferences getShared;
	private String latitude;
	private String longitude;

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1888;

	// file url to store image/video
	private Uri fileUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer_image);
		super.setTitle(getString(R.string.title_activity_home_offer));

		getShared = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);
		
		
		image = ((ImageView) findViewById(R.id.image));
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				captureImage();
			}

		});

		nextButton = ((Button) findViewById(R.id.nextButton));
		Spannable buttonLabel = new SpannableString("   Continuar");
		buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.background_offer_select,      
		    ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		nextButton.setText(buttonLabel);
		nextButton.setVisibility(View.GONE);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						AddOfferActivity.class);
//				intent.putExtra("BitmapImage",
//						AddOfferImageActivity.this.bitmap);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				AddOfferImageActivity.this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		        byte[] bytes = stream.toByteArray(); 
		        intent.putExtra("BitmapImage",bytes);
				
				
				intent.putExtra("imagePath",
						AddOfferImageActivity.this.fileUri.getPath());
				startActivity(intent);

			}
		});
		
		GPSTracker gps = new GPSTracker(this);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			this.latitude = String.valueOf(gps.getLatitude());
			this.longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
			
		}

		setUpMap();

	}
	

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = Image.getOutputMediaFileUri();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	private void setUpMap() {
		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		LatLng myLocation = new LatLng(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude));

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
		map.getUiSettings().setZoomControlsEnabled(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				bitmap = Image.previewCapturedImage(image, fileUri, true);
				image.getLayoutParams().width = 150;
				image.getLayoutParams().height = 130;
				nextButton.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(getApplicationContext(),
				HomeOfferActivity.class);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(setIntent);
	}
	

}
