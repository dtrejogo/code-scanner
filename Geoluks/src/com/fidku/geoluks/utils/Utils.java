package com.fidku.geoluks.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static final int ROBOTO_REGULAR = 1;
	public static final int ROBOTO_LIGHT = 2;

	/**
	 * return true if device is connect to internet
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isDeviceOnline(Activity activity) {
		ConnectivityManager connMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static Typeface getTypeFace(Context context, int family) {

		Typeface tf = null;
		String path = null;

		switch (family) {

		case ROBOTO_REGULAR:
			path = "fonts/Roboto-Regular.ttf";
			break;
		case ROBOTO_LIGHT:
			path = "fonts/Roboto-Light.ttf";
			break;
		}

		tf = Typeface.createFromAsset(context.getApplicationContext()
				.getAssets(), path);

		return tf;

	}

	public static Location getLastKnowLocation(Activity activity) {

		try {
			LocationManager locationm = (LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE);

			if (locationm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				return locationm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

		} catch (Exception e) {
		}
		return null;
	}

}
