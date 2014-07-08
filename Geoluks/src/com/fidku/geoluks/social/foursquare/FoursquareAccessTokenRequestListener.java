package com.fidku.geoluks.social.foursquare;

import com.fidku.geoluks.LoginActivity; 
import com.fidku.geoluks.parameters.App; 
import android.widget.Toast;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.User;

public class FoursquareAccessTokenRequestListener implements
		AccessTokenRequestListener {

	private LoginActivity loginActivity;

	public FoursquareAccessTokenRequestListener(LoginActivity loginActivity) {
		this.loginActivity=loginActivity;
	}
	@Override
	public void onError(String errorMsg) {
		Toast.makeText(loginActivity, errorMsg, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onAccessGrant(String accessToken) {
		loginActivity.getAsyncFourqueare().getUserInfo(new UserInfoRequestListener() {

			@Override
			public void onError(String errorMsg) {
				Toast.makeText(loginActivity, errorMsg, Toast.LENGTH_LONG)
						.show();

			}

			@Override
			public void onUserInfoFetched(User user) {
				// System.out.println(user.toString());
				loginActivity.sessionIniciada(user.getContact().getEmail(),
						user.getFirstName() + " " + user.getLastName(),
						user.getId() + "", App.SOCIA_NETWORK.FOURSQUARE.value); 

			}

		});

	}

}
