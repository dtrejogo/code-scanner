package com.fidku.geoluks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fidku.geoluks.adapters.FoursquareVenuesNearbyUserlessRequest;
import com.fidku.geoluks.adapters.HttpApiAdapter;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.beans.Category;
import com.fidku.geoluks.beans.Products;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.utils.DownloadImageAsync;
import com.fidku.geoluks.utils.Image;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.FoursquareVenuesResquestListener;
import br.com.condesales.models.Venue;

public class FormProductActivity extends BaseActivity implements OnKeyListener,
		OnClickListener {

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
	private EditText etTitle;
	private EditText etPrecio;
	private EditText etCodigo;
	private EditText etDescripcion;
	private Button btnEscanear;
	private Button btnGuardar;

	private ArrayList<Venue> listaLugares;
	private SharedPreferences getShared;
	private Dialog customDialog;
	private String codigo;
	private String product_id = "-1";
	private Spinner select_place;
	private ProgressDialog progressDialog;
	private TextView tvAreHere;
	private ImageView image;
	private Uri fileUri;
	private Spinner sp_categoria;
	protected ArrayList<Category> listaCategorias;
	private EditText etBrand;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_register_price);
		setTitle(getResources().getString(R.string.product_activity_title));
		progressDialog = new ProgressDialog(this);
		etBrand = (EditText) findViewById(R.id.etBrand);
		etTitle = (EditText) findViewById(R.id.etNombre);
		etPrecio = (EditText) findViewById(R.id.etPrecio);
		etCodigo = (EditText) findViewById(R.id.etCodigoBarra);
		etCodigo.setFocusable(false);
		etDescripcion = (EditText) findViewById(R.id.etDescripcion);

		btnEscanear = (Button) findViewById(R.id.btn_escanear);
		btnGuardar = (Button) findViewById(R.id.btn_guardar);

		tvAreHere = (TextView) findViewById(R.id.are_here);

		btnEscanear.setOnClickListener(this);
		btnGuardar.setOnClickListener(this);

		getShared = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);

		codigo = getIntent().getStringExtra("codigo");

		etCodigo.setText(codigo);

		((ImageButton) findViewById(R.id.btn_change_location))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						seleccionarLugar();

					}
				});

		fileUri = Image.getOutputMediaFileUri();
		image = ((ImageView) findViewById(R.id.image));
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				captureImage();

			}
		});
		requestTipsNearby("", 0);

		sp_categoria = (Spinner) findViewById(R.id.category);

		ApiConnection.getCategories(new HttpApiAdapter() {

			@Override
			public void onSuccess(boolean status, String message,
					Object response) {

				listaCategorias = new ArrayList<Category>();
				JSONArray res = (JSONArray) response;
				String[] categories = new String[res.length()];
				for (int i = 0; i < categories.length; i++) {
					try {
						listaCategorias.add(new Category(res.getJSONObject(i)
								.getJSONObject("Categories").getInt("id"), res.getJSONObject(i)
								.getJSONObject("Categories").getString(
								"name")));
						categories[i] = listaCategorias.get(i).getName();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				ArrayAdapter<String> categorias = new ArrayAdapter<String>(
						FormProductActivity.this,
						android.R.layout.simple_spinner_item, categories);

				sp_categoria.setAdapter(categorias);
				categorias.notifyDataSetChanged();

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, requestCode, data);

		switch (requestCode) {
		case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Image.previewCapturedImage(image, fileUri, true);
				System.out.println("imagen guardada en:" + fileUri.getPath());
			}
		}
	}

	private void requestTipsNearby(final String accessToken, final int intentos) {
		Location loc = new Location("");
		loc.setLatitude(Double.parseDouble(getShared.getString(
				App.LOCATION_KEY_LATITUDE, "0")));
		loc.setLongitude(Double.parseDouble(getShared.getString(
				App.LOCATION_KEY_LONGITUDE, "0")));

		/*
		 * loc.setLatitude(8.9823889); loc.setLongitude(-79.5266389);
		 */

		VenuesCriteria vCriteria = new VenuesCriteria();
		vCriteria.setLocation(loc);
		vCriteria.setRadius(10);

		new FoursquareVenuesNearbyUserlessRequest(FormProductActivity.this,
				new FoursquareVenuesResquestListener() {

					@Override
					public void onError(String errorMsg) {
						System.out.println("error al hacer el getVenuesNearby:"
								+ errorMsg);
					}

					@Override
					public void onVenuesFetched(ArrayList<Venue> venues) {
						if (venues.size() < 0 && intentos < 5) {
							requestTipsNearby(accessToken, intentos + 1);
							return;
						} else if (venues.size() < 0 && intentos == 5) {
							Toast.makeText(
									FormProductActivity.this,
									"No es posible encontrar lugares a su alrrededor",
									Toast.LENGTH_LONG).show();
							;
						}
						listaLugares = venues;
						System.out.println("Venues Feched:" + venues.toArray());
						seleccionarLugar();

					}

				}, vCriteria).execute("");

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public void seleccionarLugar() {

		customDialog = new Dialog(this, R.style.Theme_Dialog_Translucent);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCancelable(false);
		customDialog.setContentView(R.layout.dialog_places);

		TextView titulo = (TextView) customDialog
				.findViewById(R.id.titulo_dialog_place);
		titulo.setText("Lugares Cercanos");

		String[] localidades = new String[listaLugares.size()];
		int index = 0;
		for (Iterator<Venue> iterator = listaLugares.iterator(); iterator
				.hasNext();) {
			Venue venue = iterator.next();
			localidades[index] = venue.getName();
			index++;
		}

		ArrayAdapter<String> aa_paises = new ArrayAdapter<String>(
				FormProductActivity.this, android.R.layout.simple_spinner_item,
				localidades);

		select_place = (Spinner) customDialog.findViewById(R.id.place);
		// Asigno el adaptador al Spinner
		select_place.setAdapter(aa_paises);

		final Button btn_aceptar = ((Button) customDialog
				.findViewById(R.id.aceptar));
		btn_aceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				progressDialog.dismiss();
				try {
					tvAreHere.setText("Estas en: "
							+ listaLugares.get(
									select_place.getSelectedItemPosition())
									.getName());

				} catch (Exception e) {
					requestTipsNearby("", 1);
					return;
				}

				progressDialog
						.setMessage("Buscando precios en el lugar seleccionado");
				progressDialog.show();
				ApiConnection.getActionPrice(codigo, listaLugares
						.get(select_place.getSelectedItemPosition()),
						new HttpApiAdapter() {

							@Override
							public void onSuccess(boolean status,
									String message, Object response) {
								progressDialog.dismiss();

								try {
									JSONObject res = (JSONObject) response;
									Intent i = new Intent(
											FormProductActivity.this,
											ProductActivity.class);
									Products product;
									switch (res.getInt("action")) {
									case 1:
										product = new Products(res
												.getString("Product"));

										new DownloadImageAsync(image)
												.execute(product.getImageUrl());
										product_id = product.getId() + "";
										etDescripcion.setText(product
												.getDescription());
										etTitle.setText(product.getTitle());
										etDescripcion.setFocusable(false);
										etTitle.setFocusable(false);
										customDialog.dismiss();
										etPrecio.requestFocus();
										break;
									case 2:

										i.putExtra(ProductActivity.ParamAction,
												"edit");
										i.putExtra(
												ProductActivity.ParamPriceId,
												res.getString("price_id"));
										i.putExtra(
												ProductActivity.ParamPriceValue,
												res.getString("price_value"));
										i.putExtra(
												ProductActivity.ParamProduct,
												res.getString("Product"));
										startActivity(i);
										break;

									default:
										customDialog.dismiss();
										break;
									}
									// Toast.makeText(myReferer,
									// res.getString("message"),
									// Toast.LENGTH_LONG).show();

								} catch (JSONException e) {
									// TODO Auto-generated catch block

									Log.d(this.getClass().getName(),
											e.getMessage());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Log.d(this.getClass().getName(),
											e.getMessage());
								}
								System.out.println("Consultar Lugar:"
										+ response);

							}
						});
			}
		});

		customDialog.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_escanear:
			startActivity(new Intent(this, HomeActivity.class));
			break;
		case R.id.btn_guardar:

			Venue lugarSeleccionado = listaLugares.get(select_place
					.getSelectedItemPosition());
			tvAreHere.setText("Estas en: " + lugarSeleccionado.getName());
			progressDialog.dismiss();
			progressDialog.setMessage("Procesando...");
			progressDialog.show();
			final File product_image = new File(fileUri.getPath());
			if (product_id.equalsIgnoreCase("-1") && !product_image.exists()) {
				Toast.makeText(getApplicationContext(),
						"Debe realizar una foto", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				captureImage();
				return;
			}
			try {
				ApiConnection.savePrice(getShared.getString(App.KEY_USER_UID,
						App.USER_ID_DEFAULT), product_id, etTitle.getText()
						.toString(), etDescripcion.getText().toString(),
						etBrand.getText().toString(), etPrecio.getText().toString(), etCodigo
								.getText().toString(), lugarSeleccionado
								.getLocation().getLat(), lugarSeleccionado
								.getLocation().getLng(), lugarSeleccionado
								.getId(), 
								product_image,
								listaCategorias.get(sp_categoria.getSelectedItemPosition()).getId()+"", new HttpApiAdapter() {

							@Override
							public void onSuccess(boolean status,
									String message, Object response) {
								progressDialog.dismiss();

								Toast.makeText(FormProductActivity.this,
										message, Toast.LENGTH_LONG).show();
								if (status) {
									// product image is deleted

									product_image.delete();
									actualizarFeed();

								}

							}
						});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		}
	}

	protected void actualizarFeed() {
		System.out.println("HACIENDO NUEVA PETICION");
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
						Intent intent = new Intent(FormProductActivity.this,
								OkActivity.class);
						intent.putExtra(OkActivity.MSJ_OK,
								"Precio guardado exitosamente");

						startActivity(intent);
					}
				});
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
