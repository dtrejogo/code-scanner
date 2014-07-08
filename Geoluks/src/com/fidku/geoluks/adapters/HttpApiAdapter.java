 
package com.fidku.geoluks.adapters;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class HttpApiAdapter extends AsyncHttpResponseHandler {

	private final String STATUS_KEY = "status", MESSAGE_KEY = "message",
			DATA_KEY = "data";

	public HttpApiAdapter() {

	}

	@Override
	public void onSuccess(String content) {
		try {
			System.out.println("API--->" + content);

			JSONObject response = new JSONObject(content);
			try {
				
				onSuccess(response.getBoolean(STATUS_KEY),
						response.getString(MESSAGE_KEY),
						response.getJSONArray(DATA_KEY));
				System.out.println("JSON DATA ARRAY" + response.getJSONArray(DATA_KEY));
			} catch (JSONException e) {
				try {
					onSuccess(response.getBoolean(STATUS_KEY),
							response.getString(MESSAGE_KEY),
							response.getJSONObject(DATA_KEY));
					System.out.println("JSON DATA OBJECT" + response.getJSONObject(DATA_KEY));
				} catch (JSONException e2) {
					onSuccess(false, e.getMessage(), new JSONArray());
				}
				
			}

		} catch (JSONException e) {
			onSuccess(false, e.getMessage(), new JSONArray());
		} catch (Exception e) {
			onSuccess(false, e.getMessage(), new JSONArray());
		}

	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		super.onFailure(arg0, arg1, arg2, arg3);
		 
		onSuccess(false, "No fue posible realizar la accion, por favor intenta de nuevo", new JSONArray());
	}

	public abstract void onSuccess(boolean status, String message,
			Object response);

}
