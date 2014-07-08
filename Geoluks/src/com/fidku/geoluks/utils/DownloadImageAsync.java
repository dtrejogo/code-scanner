package com.fidku.geoluks.utils;

import java.io.InputStream;
import java.util.HashMap;

import com.fidku.geoluks.beans.UrlBitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class DownloadImageAsync extends AsyncTask<String, Void, UrlBitmap> {
	ImageView bmImage;
	RelativeLayout layout;
	static HashMap<String, Bitmap> listaUrl = new HashMap<String, Bitmap>();

	public DownloadImageAsync(RelativeLayout layout) {
		// this.bmImage = bmImage;
		this.layout = layout;
		// bmImage.setImageResource(android.R.drawable.ic_menu_gallery);
	}

	public DownloadImageAsync(ImageView bmImage) {
		this.bmImage = bmImage;

		// bmImage.setImageResource(android.R.drawable.ic_menu_gallery);
	}

	@Override
	protected UrlBitmap doInBackground(String... urls) {

		if (listaUrl.containsKey(urls[0])) {
			return new UrlBitmap(urls[0], listaUrl.get(urls[0]));
		}
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urls[0]).openStream();
			Bitmap temp = BitmapFactory.decodeStream(in);
			// if(temp.getWidth()>1500)
			mIcon11 = Bitmap.createScaledBitmap(temp, 520, 520, true);
		} catch (Exception e) {
			// Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		return new UrlBitmap(urls[0], mIcon11);
	}

	@Override
	protected void onPostExecute(UrlBitmap result) {
		if (!listaUrl.containsKey(result.getUrlImage())) {
			listaUrl.put(result.getUrlImage(), result.getImagen());
		}

		if (bmImage != null) {
			bmImage.setImageBitmap(result.getImagen());
		} else {
			@SuppressWarnings("deprecation")
			BitmapDrawable drawableBitmap = new BitmapDrawable(
					result.getImagen());
			layout.setBackground(drawableBitmap);
		}
	}

}
