package com.fidku.geoluks;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.adapters.swipedismiss.SwipeDismissListViewTouchListener;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.DownloadImageAsync;
import com.fidku.geoluks.utils.Messages;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HomeLoaderActivity extends FragmentActivity {
	private HomeLoaderActivity activityReffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loader);
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		activityReffer = this;
		setUpLocationClientIfNeeded();
	}

	

	@Override
	protected void onResume() {
		super.onResume();

		setUpLocationClientIfNeeded();
	}

	AlertDialog alert = null;
	LocationManager manager;
	private int intentosGPS;
	private boolean isLocations = false;
	private int LimiteEsperaGPS=3;

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
			mLocationClient = null;
		}
	}

	synchronized private void requestFeed(final Location locations) {

		SharedPreferences getShared = getSharedPreferences(
				App.KEY_SESSION_NAME, Context.MODE_PRIVATE);
		if (!getShared.contains(App.KEY_USER_UID)) {
			return;
		}

		ApiConnection.getFeedHome(getShared.getString(App.KEY_USER_UID, null),
				Double.toString(locations.getLatitude()),
				Double.toString(locations.getLongitude()),
				new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						JSONArray jsonArray = (JSONArray) response;
						SharedPreferences sharedpreferences = getSharedPreferences(
								App.KEY_SESSION_NAME, Context.MODE_PRIVATE);
						Editor editor = sharedpreferences.edit();
						editor.putString(App.HOME_INPUT_FEED,
								jsonArray.toString());
						editor.putString(App.LOCATION_KEY_LATITUDE,
								Double.toString(locations.getLatitude()));
						editor.putString(App.LOCATION_KEY_LONGITUDE,
								Double.toString(locations.getLongitude()));
						editor.commit();

						startActivity(new Intent(getApplicationContext(),
								HomeActivity.class));

					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) { 
						if(arg0==0){
							final AlertDialog.Builder builder = new AlertDialog.Builder(HomeLoaderActivity.this);
							builder.setMessage(
									"Disculpe, lamentablemente no podemos tener acceso a internet, vuelta cuendo disponga del servicio.")
									.setCancelable(false)
									.setPositiveButton("Aceptar",
											new DialogInterface.OnClickListener() {
												public void onClick(final DialogInterface dialog,
														final int id) {
													dialog.cancel();
													System.exit(0);
												}
											})
							/*
							 * .setNegativeButton("No", new DialogInterface.OnClickListener() {
							 * public void onClick(final DialogInterface dialog, final int id) {
							 * dialog.cancel(); } })
							 */
							;
							alert = builder.create();
							alert.show();
							
						}
						//super.onFailure(arg0, arg1, arg2, arg3);
						Log.d("HomeLoader","Codigo error:"+arg0);
					}
				});

	}

	private void AlertNoGps() {
		final HomeLoaderActivity myReffer = this;
		final AlertDialog.Builder builder = new AlertDialog.Builder(myReffer);
		builder.setMessage(
				"El sistema GPS esta desactivado, lo necesitas para utilizar nuestro servicio")
				.setCancelable(false)
				.setPositiveButton("Aceptar",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
		/*
		 * .setNegativeButton("No", new DialogInterface.OnClickListener() {
		 * public void onClick(final DialogInterface dialog, final int id) {
		 * dialog.cancel(); } })
		 */
		;
		alert = builder.create();
		alert.show();
		intentosGPS=0;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	/**
	 * Locations
	 */
	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	protected static final String HOME_INPUT_FEED = "HOME_EXTRA_INPUT_FEED";

	private Location myLocations;
	private GoogleMap mMap;
	private boolean firstLocations = false;
	private LocationClient mLocationClient;

	private void setUpLocationClientIfNeeded() {
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertNoGps();
			return;
		}

		if (mLocationClient == null) {
			Log.d("GPS", "Esperando conexion de gps");
			ConnectionCallbacks callbackLocation = new ConnectionCallbacks() {

				@Override
				public void onDisconnected() {
					// Do nothing
				}

				@Override
				public void onConnected(Bundle arg0) {
					mLocationClient.requestLocationUpdates(REQUEST,
							new LocationListener() {

								

								@Override
								public void onLocationChanged(Location locations) {
									Log.d("GPS", "Cambiado locations"+locations); 
									if(intentosGPS<=LimiteEsperaGPS)
									{
										Log.d("GPS","ESPERANDO MEJOR PRECISION GPS:"+locations);
										intentosGPS++;
										return;
									}else{
										Log.d("GPS","ESPERA POR PRECISION TERMINADA");
									}
										
									if (!isLocations) {
										requestFeed(locations);
										isLocations = true;

									}

								}
							}); // LocationListener

				}
			};
			mLocationClient = new LocationClient(this, callbackLocation,
					new OnConnectionFailedListener() {

						@Override
						public void onConnectionFailed(ConnectionResult arg0) {
							// Do nothing
						}
					});
			mLocationClient.connect();

		}
	}

}
