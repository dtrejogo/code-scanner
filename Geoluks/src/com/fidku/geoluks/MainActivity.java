package com.fidku.geoluks;

import br.com.condesales.constants.FoursquareConstants;

import com.fidku.geoluks.parameters.App;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String PARAM_EXIT = "salir";
	private SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main_activity);

	}

	@Override
	protected void onResume() {

		sharedpreferences = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);

		if (getIntent().hasExtra(MainActivity.PARAM_EXIT)) {

			//Toast.makeText(this, "Intentando salir", Toast.LENGTH_LONG).show();

			finish();
			System.exit(0);

		} else {
		//	Toast.makeText(this, "No salio nada" ,Toast.LENGTH_LONG).show();

			if (sharedpreferences.contains(App.KEY_USER_UID)) {
				this.finish();
				launch(HomeLoaderActivity.class);

			} else {
				this.finish();
				cleanSession();
				launch(LoginActivity.class);

			}
		}

		super.onResume();
	}

	private void launch(Class<?> class1) {
		startActivity(new Intent(this, class1));
	}

	public void cleanSession() {

		// Eliminado Session en foursquare
		SharedPreferences sharedpreferences = getSharedPreferences(
				FoursquareConstants.SHARED_PREF_FILE, Context.MODE_PRIVATE);

		sharedpreferences.edit().clear().commit();

		// Geoluks
		sharedpreferences = getSharedPreferences(App.KEY_SESSION_NAME,
				Context.MODE_PRIVATE);

		sharedpreferences.edit().clear().commit();
	}

}
