package com.fidku.geoluks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.adapters.ListOfferAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Offer;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.GPSTracker;
import com.fidku.geoluks.utils.Image;
import com.fidku.geoluks.utils.TypefaceSpan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeOfferActivity extends BaseActivity {

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1888;

	ArrayList<Offer> offerList;
	ListOfferAdapter adapter;
	int start = 0;
	int limit = 15;
	boolean loadingMore = false;
	View loadMoreView;
	ListView listViewItems;
	private SharedPreferences getShared;
	private String latitude;
	private String longitude;
	private Uri fileUri;
	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle(getString(R.string.title_activity_home_offer));
		setContentView(R.layout.activity_home_offer);

		getShared = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);
		if (getShared == null) {
			return;
		}

		listViewItems = (ListView) findViewById(R.id.listView1);

		loadMoreView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.list_view_load_more, null, false);
		listViewItems.addFooterView(loadMoreView);

		offerList = new ArrayList<Offer>();
		adapter = new ListOfferAdapter(HomeOfferActivity.this,
				R.layout.list_view_row_offer, offerList);
		listViewItems.setAdapter(adapter);

		listViewItems.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;

				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					loadOffers();
				}
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		GPSTracker gps = new GPSTracker(this);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());

			loadOffers();
		} else {
			gps.showSettingsAlert();
		}
	}

	private void loadOffers() {
		loadingMore = true;
		SharedPreferences getShared = getSharedPreferences(
				App.KEY_SESSION_NAME, Context.MODE_PRIVATE);

		ApiConnection.getOffers(getShared.getString(App.KEY_USER_UID, null),
				latitude, longitude, start, limit, new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						if (status) {

							try {
								JSONArray results = (JSONArray) response;
								if (results.length() == 0) {

									if (start == 0){
										TextView tv =  (TextView)loadMoreView.findViewById(R.id.result_message);
										tv.setText(R.string.home_offer_no_data);
									}else {
										listViewItems
												.removeFooterView(loadMoreView);
									}
								} else {

									for (int i = 0; i < results.length(); i++) {
										start++;
										JSONObject obj = results
												.getJSONObject(i);
										JSONObject offer = obj
												.getJSONObject("Offer");
										JSONObject user = obj
												.getJSONObject("User");
										JSONObject place = obj
												.getJSONObject("Place");

										Offer offerObj = new Offer();
										offerObj.setId(offer.getString("id"));
										offerObj.setTitle(offer
												.getString("title"));
										offerObj.setCreatedDate(offer
												.getString("created_date"));
										offerObj.setImageUrl(offer
												.getString("image_url"));
										offerObj.setUsername(user
												.getString("name"));
										offerObj.setFoursquareName(place
												.getString("name"));

										if (offer.getString("checked").equals(
												"1")) {
											offerObj.setChecked(true);
											Log.i("tag",
													"checked "
															+ offerObj
																	.getTitle());
										} else {
											Log.i("tag", "no checked");
											offerObj.setChecked(false);
										}

										// offers[i] = offerObj;
										offerList.add(offerObj);
										adapter.add(offerObj);

									}

									adapter.notifyDataSetChanged();
									loadingMore = false;

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.add_offer:
			// Intent setIntent = new Intent(getApplicationContext(),
			// AddOfferImageActivity.class);
			// startActivity(setIntent);
			captureImage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(getApplicationContext(),
						AddOfferActivity.class);

				intent.putExtra("imageUri", fileUri.toString());
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(getApplicationContext(),
				HomeActivity.class);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(setIntent);
	}

}
