package com.fidku.geoluks.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Image {

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Geoluks";
	private static final int IMAGE_WIDHT = 250;
	private static final int IMAGE_HEIGHT = 250;

	public static boolean loadImageFromURL(final String fileUrl,
			final ImageView iv) {
		new Thread() {
			public void run() {
				Bitmap bm = loadImageFromURL(fileUrl);
				if (bm != null) {
					iv.setImageBitmap(bm);

				}
			}
		}.start();

		return true;
	}

	public static Bitmap loadImageFromURL(String fileUrl) {
		try {

			URL myFileUrl = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();

			InputStream is = conn.getInputStream();

			return BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creating file uri to store image/video
	 */
	public static Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile() {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;

		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".png");

		return mediaFile;
	}

	/*
	 * Display image from a path to ImageView
	 */
	public static Bitmap previewCapturedImage(ImageView image, Uri fileUri,
			boolean resizeImage) {
		try {

			image.setVisibility(View.VISIBLE);

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileUri.getPath(), options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options,
					Image.IMAGE_WIDHT, Image.IMAGE_HEIGHT);
			Log.d("geoluks", "options inSampleSize " + options.inSampleSize);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory
					.decodeFile(fileUri.getPath(), options);

			if (resizeImage) {

				bitmap = saveBitmapOnFileSystem(bitmap, fileUri.getPath());

//				bitmap = adjustOrientation(fileUri.getPath());
//
//				bitmap = saveBitmapOnFileSystem(bitmap, fileUri.getPath());

			}

			image.setImageBitmap(bitmap);

			return bitmap;

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	private static Bitmap saveBitmapOnFileSystem(Bitmap bitmap, String fileUri) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(fileUri));
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);

			fos.flush();
			fos.close();
			return bitmap;

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	public static Bitmap adjustOrientation(String fileUri) {
		try {
			File f = new File(fileUri);
			ExifInterface exif = new ExifInterface(f.getPath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int angle = 0;

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				angle = 90;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				angle = 180;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				angle = 270;
			}

			Matrix mat = new Matrix();
			mat.postRotate(angle);

			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
					null, null);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), mat, true);
		} catch (IOException e) {
			Log.w("TAG", "-- Error in setting image");
		} catch (OutOfMemoryError oom) {
			Log.w("TAG", "-- OOM Error in setting image");
		}

		return null;

	}

	public static float convertDpToPixel(Activity activity, float dp) {
		DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

}
