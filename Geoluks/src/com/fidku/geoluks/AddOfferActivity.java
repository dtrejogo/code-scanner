package com.fidku.geoluks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.FoursquareVenuesResquestListener;
import br.com.condesales.models.Venue;

import com.fidku.geoluks.adapters.FoursquareVenuesNearbyUserlessRequest;
import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Offer;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.DownloadImageAsync;
import com.fidku.geoluks.utils.GPSTracker;
import com.fidku.geoluks.utils.Image;
import com.fidku.geoluks.utils.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AddOfferActivity extends BaseActivity {

	// Values for email and password at the time of the login attempt.
	private String title;
	private Bitmap bitmap;
	private String latitude;
	private String longitude;

	// UI references.
	private EditText titleView;
	private Spinner locationsView;
	private ImageView imageView;
	private View formView;
	private View mLoginStatusView;
	private TextView processingMessage;
	private Venue selectedVenue;
	private Uri fileUri;
	private ArrayList<Venue> venues;
	private Typeface robotoRegular;
	private Typeface robotoLight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer);
		super.setTitle(getString(R.string.title_activity_home_offer));

		Intent intent = getIntent();
		fileUri = Uri.parse(intent.getStringExtra("imageUri"));

		prepareView();

		bitmap = Image.previewCapturedImage(imageView, fileUri, true);
		imageView.setImageBitmap(bitmap);

		formView = findViewById(R.id.form);
		mLoginStatusView = findViewById(R.id.login_status);
		processingMessage = (TextView) findViewById(R.id.status_message);

		findViewById(R.id.add_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addOffer();
					}
				});

	}

	private void prepareView() {
		this.robotoRegular = Utils.getTypeFace(this, Utils.ROBOTO_REGULAR);
		this.robotoLight = Utils.getTypeFace(this, Utils.ROBOTO_LIGHT);
		locationsView = (Spinner) findViewById(R.id.locations);
		titleView = (EditText) findViewById(R.id.title);
		titleView.setTypeface(robotoLight);
		imageView = (ImageView) findViewById(R.id.image);
		TextView nextEditText = (TextView) findViewById(R.id.next_edit_text);
		nextEditText.setTypeface(robotoRegular);
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		GPSTracker gps = new GPSTracker(this);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			this.latitude = String.valueOf(gps.getLatitude());
			this.longitude = String.valueOf(gps.getLongitude());

			requestTipsNearby(0);
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
	}

	private void requestTipsNearby(final int intentos) {
		Location loc = new Location("");
		loc.setLatitude(Double.parseDouble(this.latitude));
		loc.setLongitude(Double.parseDouble(this.longitude));

		VenuesCriteria vCriteria = new VenuesCriteria();
		vCriteria.setLocation(loc);
		vCriteria.setRadius(10);

		new FoursquareVenuesNearbyUserlessRequest(this,
				new FoursquareVenuesResquestListener() {

					@Override
					public void onError(String errorMsg) {
						System.out.println("error al hacer el getVenuesNearby:"
								+ errorMsg);
					}

					@Override
					public void onVenuesFetched(ArrayList<Venue> venues) {
						if (venues.size() < 0 && intentos < 5) {
							requestTipsNearby(intentos + 1);
							return;
						} else if (venues.size() < 0 && intentos == 5) {
							Toast.makeText(
									getApplicationContext(),
									"No es posible encontrar lugares a su alrrededor",
									Toast.LENGTH_LONG).show();

						}

						populateDropDown(venues);

					}

				}, vCriteria).execute("");

	}

	public void populateDropDown(final ArrayList<Venue> venues) {

		this.venues = venues;
		String[] localidades = new String[venues.size()];
		int index = 0;

		for (Venue v : venues) {
			localidades[index] = v.getName();
			index++;

		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, localidades) {

			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(AddOfferActivity.this.robotoLight);

				return v;
			}

			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getDropDownView(position, convertView, parent);

				((TextView) v).setTypeface(AddOfferActivity.this.robotoLight);

				return v;
			}
		};

		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		locationsView.setAdapter(adapter);
	}

	/**
	 * try to save an offer
	 */
	public void addOffer() {

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		titleView.setError(null);

		// Store values at the time of the login attempt.
		title = titleView.getText().toString().trim();

		if (this.venues == null || this.venues.size() == 0) {
			titleView.setError(getString(R.string.error_field_required));
			focusView = locationsView;
			cancel = true;
		}

		selectedVenue = this.venues
				.get(locationsView.getSelectedItemPosition());

		if (selectedVenue == null) {
			titleView.setError(getString(R.string.error_field_required));
			focusView = locationsView;
			cancel = true;
		}

		// Check for a valid title.
		if (TextUtils.isEmpty(title)) {
			titleView.setError(getString(R.string.error_field_required));
			focusView = titleView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			processingMessage.setText(R.string.add_offer_processing);
			showProgress(true);

			SharedPreferences getShared = getSharedPreferences(
					App.KEY_SESSION_NAME, Context.MODE_PRIVATE);

			Offer offer = new Offer();
			offer.setTitle(title);
			offer.setUid(getShared.getString(App.KEY_USER_UID, null));
			offer.setFoursquareLatitude(String.valueOf(selectedVenue
					.getLocation().getLat()));
			offer.setFoursquareLongitude(String.valueOf(selectedVenue
					.getLocation().getLng()));
			offer.setFoursquareId(String.valueOf(selectedVenue.getId()));
			offer.setFoursquareName(selectedVenue.getName());

			File file = new File(fileUri.getPath());

			try {
				ApiConnection.saveOffer(offer, file, new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						String successMessage;
						if (status) {
							successMessage = getResources().getString(
									R.string.add_offer_success);
						} else {
							successMessage = message;
						}

						Toast.makeText(getApplicationContext(), successMessage,
								Toast.LENGTH_LONG).show();

						startActivity(new Intent(getApplicationContext(),
								HomeOfferActivity.class));
						finish();
					}
				});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			formView.setVisibility(View.VISIBLE);
			formView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							formView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			formView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(getApplicationContext(),
				HomeOfferActivity.class);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(setIntent);
	}

}
