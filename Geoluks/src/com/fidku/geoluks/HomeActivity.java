package com.fidku.geoluks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.fidku.geoluks.adapters.HomeItemAdapter;
import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Price;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.listener.HomeListener;
import com.fidku.geoluks.listener.HomeSwipeListViewListener;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.Image;
import com.fidku.geoluks.utils.Utils;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class HomeActivity extends BaseActivity {

	protected static final String ACTION_UPDATE_FEED = "UPDATE_FEED";
	private ListView productFeed;
	private HomeActivity refererHomeActivity;
	private SharedPreferences getShared;

	SwipeListView swipelistview;
	HomeItemAdapter adapter;
	List<Products> itemData;
	private ImageButton btn_offer;
	private ImageButton btn_compare;
	private ImageButton btn_take;
	private ImageButton btn_list;
	private ImageButton btn_profile;

	private SwipeRefreshLayout swipeRefresh;

	private SoundPool soundPool;
	private int soundID;
	private boolean loaded = false;
	private HomeListener listener;
	private HomeSwipeListViewListener swipeListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getResources().getString(R.string.home_activity_title));
		setContentView(R.layout.home_activity);
		setDisplayHomeEnable(false);
		// productFeed = (ListView) findViewById(R.id.feed_home);

		btn_offer = (ImageButton) findViewById(R.id.btn_offer);
		btn_compare = (ImageButton) findViewById(R.id.btn_compare);
		btn_take = (ImageButton) findViewById(R.id.btn_take);
		btn_list = (ImageButton) findViewById(R.id.btn_list);
		btn_profile = (ImageButton) findViewById(R.id.btn_profile);

		listener = new HomeListener(this);
		btn_offer.setOnClickListener(listener);
		btn_offer.setOnTouchListener(listener);

		btn_compare.setOnClickListener(listener);
		btn_take.setOnClickListener(listener);
		btn_list.setOnClickListener(listener);
		btn_profile.setOnClickListener(listener);

		refererHomeActivity = this;

		getShared = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);
		if (getShared == null) {
			return;
		}

		// requestFeed();
		if (mMap == null) {
			setUpMapIfNeeded();
		}

		// swipe refresh
		swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Reproduccion de sonido
				if (loaded) {
					soundPool.play(soundID, 0.9f, 0.9f, 1, 0, 1f);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						requestHomeFeed(true);
					}
				}, 1000);

			}
		});
		swipeRefresh
				.setColorScheme(android.R.color.holo_blue_dark,
						android.R.color.holo_orange_light,
						android.R.color.holo_green_dark,
						android.R.color.holo_red_light);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = soundPool.load(this, R.raw.ping, 1);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		swipelistview = (SwipeListView) findViewById(R.id.example_swipe_lv_list);
		itemData = new ArrayList<Products>();

		adapter = new HomeItemAdapter(this, R.layout.product_feed_home,
				itemData);

		setSwipeListener(new HomeSwipeListViewListener(this, swipelistview));
		swipelistview.setSwipeListViewListener(getSwipeListener());

		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		swipelistview.setOffsetLeft(Image.convertDpToPixel(this, 190f));
		swipelistview.setOffsetRight(Image.convertDpToPixel(this, 0f));
		swipelistview.setAnimationTime(100); // Animation time
		swipelistview.setSwipeOpenOnLongPress(true);
		swipelistview.setAdapter(adapter);

		if (getIntent().getStringExtra(ACTION_UPDATE_FEED) != null) {
			requestHomeFeed(false);
		}
	}

	public void setCkeckPrice(final int position, final boolean confirm) {

		int price_id = itemData.get(position).getPrice().getId();

		String uid = getShared.getString(App.KEY_USER_UID, null);
		if (uid != null) {
			final ImageButton btnUndo = (ImageButton) swipelistview.getChildAt(
					position).findViewById(R.id.btn_undo);
			final TextView tv_msj = (TextView) swipelistview.getChildAt(
					position).findViewById(R.id.tv_msj);
			btnUndo.setVisibility(View.INVISIBLE);
			swipelistview.getChildAt(position)
					.findViewById(R.id.progress_confirm)
					.setVisibility(View.VISIBLE);
			tv_msj.setText(confirm ? "Confirmando...  " : "Desconfirmando..  ");
			final HomeActivity homeRefer = this;
			ApiConnection.getCheckPrice(uid, price_id + "", null, confirm,
					new HttpApiAdapter() {

						@Override
						public void onSuccess(boolean status, String message,
								Object jsonArray) {
							if (status) {
								if (confirm) {
									btnUndo.setVisibility(View.VISIBLE);
									tv_msj.setText(message);
									swipelistview
											.getChildAt(position)
											.findViewById(R.id.progress_confirm)
											.setVisibility(View.INVISIBLE);
									swipelistview.closeSwipeMenuIn(position,
											App.TIME_AFTER_CONFIRM);
								} else {
									swipelistview.closeAnimate(position);
								}

							} else {
								new AlertDialog.Builder(homeRefer)
										.setIcon(
												android.R.drawable.ic_dialog_alert)
										.setTitle("Geoluks")
										.setMessage(message)
										.setPositiveButton(
												android.R.string.ok,
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.cancel();
														swipelistview
																.closeAnimate(position);

													}
												}).show();
							}

						}
					});
		} else {
			launchMain();
		}

	}

	private void launchMain() {
		startActivity(new Intent(refererHomeActivity, MainActivity.class));

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(this.getClass().getName(),
				"UserLocation: "
						+ getShared.getString(App.LOCATION_KEY_LATITUDE, "0")
						+ ","
						+ getShared.getString(App.LOCATION_KEY_LONGITUDE, "0"));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(Double.parseDouble(getShared.getString(
						App.LOCATION_KEY_LATITUDE, "0")), Double
						.parseDouble(getShared.getString(
								App.LOCATION_KEY_LONGITUDE, "0"))), 15));

		requestFeed(false);
	}

	private void requestFeed(boolean updateGPS) {

		Log.d(this.getClass().getName(), "ACTUALIZANDO FEED");
		if (!getShared.contains(App.KEY_USER_UID)) {
			return;
		}
		String feed = getShared.getString(App.HOME_INPUT_FEED, null);
		if (feed == null) {
			return;
		}
		itemData.clear();
		adapter.notifyDataSetChanged();
		final ArrayList<Products> products = Products.getProducts(feed);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (Products product : products) {
					itemData.add(product);
					mMap.addMarker(new MarkerOptions()
							.position(
									new LatLng(product.getPlace().getLat(),
											product.getPlace().getLog()))
							.title(product.getTitle() + " - $"
									+ product.getPrice().getValue())
							.snippet(product.getDescription()));
				}
				adapter.notifyDataSetChanged();
				swipeRefresh.setRefreshing(false);
			}
		});

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	/**
	 * Locations
	 */

	private GoogleMap mMap;
	private boolean firstLocations = false;

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				// We will provide our own zoom controls.
				mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.setMyLocationEnabled(true);
				mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						Toast.makeText(refererHomeActivity, marker.getTitle(),
								Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}

	public void onClickInMap() {
		Toast.makeText(this, "Hizo Clic  en el mapa", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Geoluks")
					.setMessage("Seguro que deseas salir?")
					.setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									System.exit(0);
								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void editarPrice(final int position) {
		final Dialog customDialog = new Dialog(this,
				R.style.Theme_Dialog_Translucent);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCancelable(false);
		customDialog.setContentView(R.layout.dialog);

		final Products product = itemData.get(position);
		TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
		titulo.setText("Editar Precio");

		TextView contenido = (TextView) customDialog
				.findViewById(R.id.contenido);
		contenido.setText(product.getTitle());

		final EditText et_precio = (EditText) customDialog
				.findViewById(R.id.et_precio);
		et_precio.setText(product.getPrice().getValue() + "");
		final Button btn_aceptar = ((Button) customDialog
				.findViewById(R.id.aceptar));
		btn_aceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String newPrice = et_precio.getText().toString();
				et_precio.setEnabled(false);
				btn_aceptar.setText("Editando...");
				btn_aceptar.setEnabled(false);

				ApiConnection.getEditPrice(
						getShared.getString(App.KEY_USER_UID, null),
						Integer.toString(product.getPrice().getId()), newPrice,
						new HttpApiAdapter() {

							@Override
							public void onSuccess(boolean status,
									String message, Object response) {

								if (status) {
									Price price = product.getPrice();
									price.setValue(Double.parseDouble(et_precio
											.getText().toString()));
									product.setPrice(price);
									itemData.set(position, product);
									adapter.notifyDataSetChanged();
									refererHomeActivity.requestHomeFeed(false);
								}
								customDialog.dismiss();
								Toast.makeText(refererHomeActivity, message,
										Toast.LENGTH_SHORT).show();
							}
						});

			}
		});

		((Button) customDialog.findViewById(R.id.cancelar))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						customDialog.dismiss();
						Toast.makeText(refererHomeActivity, R.string.cancelar,
								Toast.LENGTH_SHORT).show();

					}
				});

		customDialog.show();
	}

	protected void requestHomeFeed(final boolean updateGPS) {

		if (updateGPS) {
			if (!updateGPS())
				return;
		}
		ApiConnection.getFeedHome(getShared.getString(App.KEY_USER_UID, null),
				getShared.getString(App.LOCATION_KEY_LATITUDE, null),
				getShared.getString(App.LOCATION_KEY_LONGITUDE, null),
				new HttpApiAdapter() {

					@Override
					public void onSuccess(boolean status, String message,
							Object response) {
						JSONArray jsonArray = (JSONArray) response;

						Editor editor = getShared.edit();
						editor.putString(App.HOME_INPUT_FEED,
								jsonArray.toString());
						editor.commit();
						requestFeed(updateGPS);
					}
				});
	}

	private boolean updateGPS() {

		try {
			Location location = Utils.getLastKnowLocation(this);
			if (location == null)
				return false;
			Log.d("UpdateGPS", location.toString());
			Editor editor = getShared.edit();
			editor.putString(App.LOCATION_KEY_LATITUDE,
					Double.toString(location.getLatitude()));
			editor.putString(App.LOCATION_KEY_LONGITUDE,
					Double.toString(location.getLongitude()));
			editor.commit();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	public ImageButton getBtn_offer() {
		return btn_offer;
	}

	public ImageButton getBtn_compare() {
		return btn_compare;
	}

	public ImageButton getBtn_take() {
		return btn_take;
	}

	public ImageButton getBtn_list() {
		return btn_list;
	}

	public ImageButton getBtn_profile() {
		return btn_profile;
	}

	public ListView getProductFeed() {
		return productFeed;
	}

	public HomeActivity getRefererHomeActivity() {
		return refererHomeActivity;
	}

	public SharedPreferences getGetShared() {
		return getShared;
	}

	public SwipeListView getSwipelistview() {
		return swipelistview;
	}

	public HomeItemAdapter getAdapter() {
		return adapter;
	}

	public List<Products> getItemData() {
		return itemData;
	}

	public GoogleMap getmMap() {
		return mMap;
	}

	public boolean isFirstLocations() {
		return firstLocations;
	}

	public HomeSwipeListViewListener getSwipeListener() {
		return swipeListener;
	}

	public void setSwipeListener(HomeSwipeListViewListener swipeListener) {
		this.swipeListener = swipeListener;
	}

}
