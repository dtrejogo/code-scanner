package com.fidku.geoluks.listener;

import java.util.Arrays;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.OpenRequest;
import com.fidku.geoluks.LoginActivity;
import com.fidku.geoluks.R;
import com.fidku.geoluks.social.foursquare.FoursquareAccessTokenRequestListener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivityListenet implements OnClickListener {
	private LoginActivity loginView;

	public LoginActivityListenet(LoginActivity loginActivity) {
		loginView = loginActivity;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGoogleP:
			loginView.getUsername();
			break;
		case R.id.btnFoursquare:
			loginView.getAsyncFourqueare().requestAccess(
					new FoursquareAccessTokenRequestListener(loginView));
			break;
			
		case R.id.btnFacebook:
			openActiveSession(loginView, true, loginView.getStatusCallback());
			
			break;

		default:
			break;
		}

	}
	
	private  Session openActiveSession(Activity activity,
			boolean allowLoginUI, Session.StatusCallback statusCallback) {
		OpenRequest openRequest = new OpenRequest(activity);
		openRequest.setPermissions(Arrays.asList("email"));
		openRequest.setCallback(statusCallback);

		Session session = new Session.Builder(activity).build();

		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())
				|| allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);

			return session;
		}

		return null;
	}

}
