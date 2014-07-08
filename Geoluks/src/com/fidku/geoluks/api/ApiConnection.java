package com.fidku.geoluks.api;

import java.io.File;
import java.io.FileNotFoundException;

import android.os.Build;
import android.util.Log;
import br.com.condesales.models.Venue;

import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.beans.List;
import com.fidku.geoluks.beans.Offer;
import com.fidku.geoluks.beans.Products;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiConnection {

	private static AsyncHttpClient connection;
	private static String URL_BASE;

	public static enum REQUEST_API {
		/**
		 * LOGIN ROUTING
		 * */
		LOGIN_CHECK("security/checklogin"),

		/**
		 * HOME ROUTING
		 * */
		HOME_FEED("home/feed"), HOME_CHECK_PRICE("home/check_product"), HOME_EDIT_PRICE(
				"home/edit_price"), HOME_SAVE_PRICE("home/save_price"), HOME_CHECK_PLACE(
				"home/check_place"),

		/**
		 * Offer Api
		 */
		OFFER_SAVE_OFFER("offer/saveOffer"), OFFER_GET_OFFERS("offer/getOffers"), SAVE_IMAGE(
				"home/save_image"), OFFER_CHECK_OFFER("offer/checkOffer"),

		/**
		 * List Api
		 */
		LIST_SAVE_LIST("list/saveList"), LIST_GET_LIST_CATEGORY(
				"list/getListCategory"),

		/**
		 * PRODUCT VIEW ROUTING
		 */

		PRODUCT_PLACES("product/product_place"), /**
		 * PRODUCT VIEW ROUTING
		 */

		PRODUCT_CATEGORIES("product/categories"), PRODUCT_BY_CATEGORY_ID(
				"product/category"),

		;

		public String value;

		private REQUEST_API(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	};

	static {
		connection = new AsyncHttpClient();
		//URL_BASE = "http://api.geoluks.com/";
		// URL_BASE = "http://192.168.1.22/geoluks/";
		 URL_BASE = "http://192.168.1.139/geoluks/";

	}

	public static void getRequest(REQUEST_API uri,
			AsyncHttpResponseHandler handler) {
		if (connection != null) {
			System.out.println("URL:" + URL_BASE + uri.value);
			connection.get(URL_BASE + uri.value, handler);
		}
	}

	public static void postRequest(REQUEST_API uri, RequestParams params,
			AsyncHttpResponseHandler handler) {
		if (connection != null) {
			System.out.println("Haciendo Peticion post");
			System.out.println(URL_BASE + uri.value);
			System.out.println(params.toString());

			connection.post(URL_BASE + uri.value, params, handler);
		}
	}

	/**
	 * This function is a shortcut of Login Petitions
	 * 
	 * @param email
	 * @param name
	 * @param uid
	 * @param social
	 * @param handler
	 */
	public static void loginRequest(String email, String name, String uid,
			String social, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("email", email);
		params.add("name", name);
		params.add("uid", uid);
		params.add("social", social);
		postRequest(REQUEST_API.LOGIN_CHECK, params, handler);
	}

	public static void getFeedHome(String uid, String latitude,
			String longitude, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();

		params.add("uid", uid);
		params.add("latitude", latitude);
		params.add("longitude", longitude);
		postRequest(REQUEST_API.HOME_FEED, params, handler);
	}

	public static void getCheckPrice(String uid, String price_id,
			String offer_id, boolean confirm, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("uid", uid);
		params.add("confirm", Boolean.toString(confirm));
		params.add("price_id", price_id == null ? "null" : price_id);
		params.add("offer_id", offer_id == null ? "null" : offer_id);
		postRequest(REQUEST_API.HOME_CHECK_PRICE, params, handler);
	}

	public static void getEditPrice(String uid, String price_id,
			String new_value, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("uid", uid);
		params.add("price_id", price_id);
		params.add("new_value", new_value);
		postRequest(REQUEST_API.HOME_EDIT_PRICE, params, handler);
	}

	public static void savePrice(String uid, String product_id, String title,
			String description, String brand, String price, String barcode,
			double lat, double lng, String place_id, File file,
			String categoryId, AsyncHttpResponseHandler handler)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		params.add("uid", uid);
		params.add("id", product_id);
		params.add("title", title);
		params.add("description", description);
		params.add("brand", brand);
		params.add("barcode", barcode);

		params.add("category_id", categoryId);
		if (product_id.equalsIgnoreCase("-1")) {
			params.put("imagen", file, "image/jpeg");
		}
		params.add("price_value", price);
		params.add("place_id", place_id);

		postRequest(REQUEST_API.HOME_SAVE_PRICE, params, handler);
	}

	public static void getActionPrice(String codigo, Venue venue,
			HttpApiAdapter handler) {
		RequestParams params = new RequestParams();
		params.add("barcode", codigo);
		params.add("foursquare_id", venue.getId());
		params.add("place_name", venue.getName());
		params.add("long", Double.toString(venue.getLocation().getLng()));
		params.add("lat", Double.toString(venue.getLocation().getLat()));
		postRequest(REQUEST_API.HOME_CHECK_PLACE, params, handler);

	}

	public static void saveOffer(Offer offer, File image,
			AsyncHttpResponseHandler handler) throws FileNotFoundException {
		RequestParams params = new RequestParams();

		if (offer.getId() == null) {

			params.add("uid", offer.getUid());
			params.add("title", offer.getTitle());
			params.add("lat", offer.getFoursquareLatitude());
			params.add("long", offer.getFoursquareLongitude());
			params.add("foursquare_id", offer.getFoursquareId());
			params.add("foursquare_name", offer.getFoursquareName());

		} else {
			params.add("uid", offer.getUid());
			params.add("title", offer.getTitle());
			params.add("offer_id", offer.getId());
		}

		if (image != null) {
			params.put("image", image, "image/jpeg");
		}

		postRequest(REQUEST_API.OFFER_SAVE_OFFER, params, handler);
	}

	public static void checkOffer(Offer offer, HttpApiAdapter handler) {
		RequestParams params = new RequestParams();

		params.add("uid", offer.getUid());
		params.add("offer_id", offer.getId());
		params.add("lat", offer.getFoursquareLatitude());
		params.add("long", offer.getFoursquareLongitude());
		postRequest(REQUEST_API.OFFER_CHECK_OFFER, params, handler);

	}

	/**
	 * Product request return list prices in all places
	 * 
	 * @param product_id
	 * @param handler
	 */
	public static void getProductPlaces(String product_id,
			AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("product_id", product_id);
		postRequest(REQUEST_API.PRODUCT_PLACES, params, handler);
	}

	public static void getOffers(String uid, String lat, String lng, int start,
			int limit, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("uid", uid);
		params.add("lat", lat);
		params.add("long", lng);
		params.add("start", String.valueOf(start));
		params.add("limit", String.valueOf(limit));
		postRequest(REQUEST_API.OFFER_GET_OFFERS, params, handler);
	}

	public static void saveList(List list, HttpApiAdapter handler) {
		RequestParams params = new RequestParams();

		params.add("name", list.getName());
		params.add("category_id", list.getCategory().getId());
		params.add("uid", list.getUser().getId());
		
		Log.i("tag",list.getName());
		Log.i("tag", list.getCategory().getId());
		Log.i("tag",list.getUser().getId());
		
		postRequest(REQUEST_API.LIST_SAVE_LIST, params, handler);
	}

	public static void getListCategory(HttpApiAdapter httpApiAdapter) {
		getRequest(REQUEST_API.LIST_GET_LIST_CATEGORY, httpApiAdapter);
	}

	/**
	 * 
	 * @param httpApiAdapter
	 */

	public static void getCategories(HttpApiAdapter httpApiAdapter) {
		getRequest(REQUEST_API.PRODUCT_CATEGORIES, httpApiAdapter);
	}

	public static void getProductByCategory(String categoryId,
			AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("category_id", categoryId);
		postRequest(REQUEST_API.PRODUCT_BY_CATEGORY_ID, params, handler);
	}

	/**
	 * @return the connection
	 */
	public static AsyncHttpClient getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public static void setConnection(AsyncHttpClient connection) {
		ApiConnection.connection = connection;
	}

	/**
	 * @return the uRL_BASE
	 */
	public static String getURL_BASE() {
		return URL_BASE;
	}

	/**
	 * @param uRL_BASE
	 *            the uRL_BASE to set
	 */
	public static void setURL_BASE(String uRL_BASE) {
		URL_BASE = uRL_BASE;
	}

	public static boolean isRunningOnEmulator() {
		boolean result = //
		Build.FINGERPRINT.startsWith("generic")//
				|| Build.FINGERPRINT.startsWith("unknown")//
				|| Build.MODEL.contains("google_sdk")//
				|| Build.MODEL.contains("Emulator")//
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion");
		if (result)
			return true;
		result |= Build.BRAND.startsWith("generic")
				&& Build.DEVICE.startsWith("generic");
		if (result)
			return true;
		result |= "google_sdk".equals(Build.PRODUCT);
		return result;
	}

}
