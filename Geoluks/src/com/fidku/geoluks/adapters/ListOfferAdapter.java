package com.fidku.geoluks.adapters;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.fidku.geoluks.R;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Offer;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.DownloadImageAsync;
import com.fidku.geoluks.utils.GPSTracker;
import com.fidku.geoluks.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListOfferAdapter extends ArrayAdapter<Offer> {

	private int resource;
	private Context context;
	private ArrayList<Offer> data;
	private Typeface robotoRegular;
	private Typeface robotoLight;

	public ListOfferAdapter(Context context, int resource, ArrayList<Offer> data) {
		super(context, resource, data);

		this.resource = resource;
		this.context = context;
		this.data = new ArrayList<Offer>();
		this.data.addAll(data);

		robotoRegular = Utils.getTypeFace(context, Utils.ROBOTO_REGULAR);
		robotoLight = Utils.getTypeFace(context, Utils.ROBOTO_LIGHT);

	}

	public void add(Offer offer) {
		this.data.add(offer);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		/*
		 * The convertView argument is essentially a "ScrapView" as described is
		 * Lucas post
		 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
		 * It will have a non-null value when ListView is asking you recycle the
		 * row layout. So, when convertView is not null, you should simply
		 * update its contents instead of inflating a new row layout.
		 */
		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(resource, parent, false);

			holder = new ViewHolder();
			holder.titleView = (TextView) convertView.findViewById(R.id.title);
			holder.titleView.setTypeface(robotoRegular);
			holder.usernameView = (TextView) convertView
					.findViewById(R.id.username);
			holder.usernameView.setTypeface(robotoLight);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.layout);
			holder.locationView = (TextView) convertView
					.findViewById(R.id.location);
			holder.locationView.setTypeface(robotoLight);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			holder.editOfferButton = (ImageButton) convertView
					.findViewById(R.id.edit_offer);
			holder.checkOfferButton = (ImageButton) convertView
					.findViewById(R.id.check_offer);
			holder.shareOfferButton = (ImageButton) convertView
					.findViewById(R.id.share_offer);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			// avoid flickering with this line,set default image
			Drawable d = ((Activity) context).getResources().getDrawable(
					android.R.drawable.ic_menu_gallery);
			holder.layout.setBackground(d);
		}

		// object item based on the position
		final Offer offer = data.get(position);

		this.setTitle(holder, offer.getTitle());

		holder.usernameView.setText(offer.getUsername());
		holder.locationView.setText(offer.getFoursquareName());

		if (offer.isChecked()) {
			holder.checkOfferButton.setSelected(true);
		} else {
			holder.checkOfferButton.setSelected(false);
		}

		holder.editOfferButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callEditOfferDialog(context, offer, holder);

			}
		});

		holder.checkOfferButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkOffer(offer, holder);
			}

		});

		holder.shareOfferButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT,
						offer.getTitle()+ " en Geoluks.");
				sendIntent.setType("text/plain");
				context.startActivity(Intent.createChooser(sendIntent, context
						.getResources().getText(R.string.geoluks_app_name)));

			}
		});

		new DownloadImageAsync(holder.layout).execute(offer.getImageUrl());

		return convertView;

	}

	private void checkOffer(final Offer offer, final ViewHolder holder) {
		// TODO Auto-generated method stub
		SharedPreferences getShared = this.context.getSharedPreferences(
				App.KEY_SESSION_NAME, Context.MODE_PRIVATE);

		GPSTracker gps = new GPSTracker(this.context);
		offer.setFoursquareLatitude(String.valueOf(gps.getLatitude()));
		offer.setFoursquareLongitude(String.valueOf(gps.getLongitude()));
		offer.setUid(getShared.getString(App.KEY_USER_UID, null));

		ApiConnection.checkOffer(offer, new HttpApiAdapter() {

			@Override
			public void onSuccess(boolean status, String message,
					Object response) {
				String successMessage;
				if (status) {
					if (message.equals("checked")) {
						holder.checkOfferButton.setSelected(true);
						offer.setChecked(true);
					} else {
						holder.checkOfferButton.setSelected(false);
						offer.setChecked(false);
					}
				} else {
					Toast.makeText(ListOfferAdapter.this.context, message, Toast.LENGTH_SHORT).show();;
				}

			}
		});

	}

	/**
	 * open dialog to edit offer
	 * 
	 * @param context
	 * @param offer
	 */
	private void callEditOfferDialog(final Context context, final Offer offer,
			final ViewHolder holder) {

		LayoutInflater layoutInflater = LayoutInflater.from(context);

		View promptView = layoutInflater.inflate(R.layout.dialog_edit_offer,
				null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(promptView);
		final EditText title = (EditText) promptView.findViewById(R.id.title);
		title.setText(offer.getTitle());

		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						saveOffer(title, offer, context, holder);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();

		alertD.show();

	}

	public void saveOffer(EditText titleView, Offer oldOffer,
			final Context context, final ViewHolder holder) {

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		titleView.setError(null);

		// Store values at the time of the login attempt.
		String title = titleView.getText().toString().trim();

		// Check for a valid title.
		if (TextUtils.isEmpty(title)) {
			titleView
					.setError(context.getString(R.string.error_field_required));
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

			SharedPreferences getShared = context.getSharedPreferences(
					App.KEY_SESSION_NAME, Context.MODE_PRIVATE);

			final Offer offer = new Offer();
			offer.setTitle(title);
			offer.setId(oldOffer.getId());
			offer.setUid(getShared.getString(App.KEY_USER_UID, null));

			try {
				ApiConnection.saveOffer(offer, null, new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						String successMessage;
						if (status) {
							successMessage = context.getResources().getString(
									R.string.edit_offer_success);
							ListOfferAdapter.this.setTitle(holder,
									offer.getTitle());
						} else {
							successMessage = message;
						}

						Toast.makeText(context, successMessage,
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * Set custom title to view
	 * 
	 * @param holder
	 * @param title
	 */
	private void setTitle(ViewHolder holder, String title) {
		holder.titleView.setText(Html.fromHtml("<u>" + title + "</u>"));
	}

	static class ViewHolder {
		private TextView titleView;
		private TextView usernameView;
		private TextView locationView;
		private ImageView imageView;
		private RelativeLayout layout;
		private ImageButton editOfferButton;
		private ImageButton checkOfferButton;
		private ImageButton shareOfferButton;
	}

}
