package com.fidku.geoluks;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.condesales.EasyFoursquareAsync;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Session.OpenRequest;
import com.fidku.geoluks.api.ApiConnection;
import com.fidku.geoluks.listener.LoginActivityListenet;
import com.fidku.geoluks.parameters.App;
import com.fidku.geoluks.social.facebook.FacebookStatusCallback;
import com.fidku.geoluks.social.google.GetNameInForeground;
import com.fidku.geoluks.utils.Utils;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity  {

	private Button btnGoogle;
	private Button btnForqueare;
	private EasyFoursquareAsync asyncFourqueare;
	private Button btnFacebook;
	private LoginActivityListenet listener;
	private TextView tv_social_msj;
	private TextView tv_term_conditions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_layout);

		Typeface robotoRegular = Utils.getTypeFace(getApplicationContext(), Utils.ROBOTO_REGULAR);
		Typeface robotoLight = Utils.getTypeFace(getApplicationContext(), Utils.ROBOTO_LIGHT);
		statusCallback = new FacebookStatusCallback(this);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);

			}
			Session.setActiveSession(session);
			 
		}
		tv_social_msj=(TextView)findViewById(R.id.tv_social_msj);
		tv_term_conditions=(TextView)findViewById(R.id.tv_term_conditions);
		
		
		// google button
		btnGoogle = (Button) findViewById(R.id.btnGoogleP);
		// foursquare button
		btnForqueare = (Button) findViewById(R.id.btnFoursquare);
		asyncFourqueare = new EasyFoursquareAsync(this);
		// facebook button
		btnFacebook = (Button) findViewById(R.id.btnFacebook);

		// listener to buttons
		listener = new LoginActivityListenet(this);

		btnGoogle.setOnClickListener(listener);
		
		btnForqueare.setOnClickListener(listener);
		btnFacebook.setOnClickListener(listener);

		tv_social_msj.setTypeface(robotoLight);
		tv_term_conditions.setTypeface(robotoLight);
		btnFacebook.setTypeface(robotoLight);
		btnForqueare.setTypeface(robotoLight);
		btnGoogle.setTypeface(robotoLight);
		
		
	}

	

	public void sessionIniciada(final String email, final String name,
			final String uid, final String social) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final ProgressDialog connectionProgressDialog = new ProgressDialog(
						LoginActivity.this);
				if (Utils.isDeviceOnline(LoginActivity.this)) {
					connectionProgressDialog.setMessage("Conectando...");
					connectionProgressDialog.show();
					ApiConnection.loginRequest(email, name, uid, social,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(String content) {
									connectionProgressDialog.cancel();
									try {

										JSONObject obj = new JSONObject(content);

										if (obj.getBoolean("status")) {

											SharedPreferences sharedpreferences = getSharedPreferences(
													App.KEY_SESSION_NAME,
													Context.MODE_PRIVATE);

											Editor editor = sharedpreferences
													.edit();

											editor.putString(
													App.KEY_USER_UID,
													obj.getJSONObject("data")
															.getJSONObject(
																	"User")
															.getString("id"));
											editor.putString(App.KEY_USER_NAME,
													name);
											editor.putString(
													App.KEY_USER_EMAIL, email);
											editor.commit();
											LoginActivity.this
													.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															startActivity(new Intent(
																	LoginActivity.this,
																	HomeLoaderActivity.class));
														}
													});

										} else {
											AlertDialog.Builder ab = new AlertDialog.Builder(
													LoginActivity.this);
											ab.setPositiveButton(
													"Aceptar",
													new DialogInterface.OnClickListener() {
														public void onClick(
																final DialogInterface dialog,
																final int id) {
															dialog.cancel();
														}
													});
											ab.setMessage(obj
													.getString("message"));
											AlertDialog alert = ab.create();
											alert.show();

										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

								}

							});
				} else {
					connectionProgressDialog
							.setMessage("El dispositivo no se puede conectar a internet");
					// mOut.setText("El dispositivo no se puede conectar a internet");
				}

			}
		});

	}

	@Override
	public void onStart() {
		super.onStart();
		/**
		 * Facebook Session Verify
		 */
		Session session = Session.getActiveSession();
		//
		session.addCallback(statusCallback);

		/**
		 * Google+ Session verify
		 */

	}

	@Override
	public void onStop() {
		super.onStop();
		/**
		 * Facebook remove callBack Session
		 */
		Session.getActiveSession().removeCallback(statusCallback);

		/**
		 * Google+ Close Session
		 */

	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Geoluks")
					.setMessage("Estas a punto de salir, deseas continuar?")
					.setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = new Intent(LoginActivity.this,
											MainActivity.class);
									i.putExtra(MainActivity.PARAM_EXIT, "true");
									startActivity(i);
								}
							}).show();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		/**
		 * Facebook Result
		 */
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		if (data != null)
			Log.d("Activity result", "  " + requestCode + "   " + resultCode
					+ "    " + data.toString());

		/**
		 * Google+ Result Connection
		 */

		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Debe seleccionar una cuenta",
						Toast.LENGTH_SHORT).show();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			handleAuthorizeResult(resultCode, data);
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * GOOGLE CONSTANCES & FIELDS
	 */
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	private String mEmail;
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

	/**
	 * Google+ Listener implementations
	 */

	private void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			// show("Unknown error, click the button again");
			return;
		}
		if (resultCode == RESULT_OK) {
			Log.i("GOOGLE+", "Retrying");
			new GetNameInForeground(LoginActivity.this, mEmail, SCOPE)
					.execute();
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			// show("User rejected authorization.");
			return;
		}
		// show("Unknown error, click the button again");
	}

	public void handleException(final UserRecoverableAuthException e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not
					// present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException) e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							statusCode, LoginActivity.this,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet
					// granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e)
							.getIntent();
					startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});

	}

	/**
	 * Attempt to get the user name. If the email address isn't known yet, then
	 * call pickUserAccount() method so the user can pick an account.
	 */
	public void getUsername() {
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (Utils.isDeviceOnline(this)) {
				new GetNameInForeground(this, mEmail, SCOPE).execute();

			} else {
				Toast.makeText(this, "No network connection available",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Starts an activity in Google Play Services so the user can pick an
	 * account
	 */
	private void pickUserAccount() {
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	/**
	 * Callback method to verify facebook session
	 */

	Session.StatusCallback statusCallback;

	public Button getBtnGoogle() {
		return btnGoogle;
	}

	public void setBtnGoogle(Button btnGoogle) {
		this.btnGoogle = btnGoogle;
	}

	public Button getBtnForqueare() {
		return btnForqueare;
	}

	public void setBtnForqueare(Button btnForqueare) {
		this.btnForqueare = btnForqueare;
	}

	public EasyFoursquareAsync getAsyncFourqueare() {
		return asyncFourqueare;
	}

	public void setAsyncFourqueare(EasyFoursquareAsync asyncFourqueare) {
		this.asyncFourqueare = asyncFourqueare;
	}

	public String getmEmail() {
		return mEmail;
	}

	public void setmEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public Session.StatusCallback getStatusCallback() {
		return statusCallback;
	}

	public void setStatusCallback(Session.StatusCallback statusCallback) {
		this.statusCallback = statusCallback;
	}

}
