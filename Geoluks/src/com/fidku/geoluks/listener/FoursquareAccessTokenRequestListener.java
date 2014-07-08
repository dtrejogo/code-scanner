package com.fidku.geoluks.listener;

import com.facebook.LoginActivity;

import br.com.condesales.listeners.AccessTokenRequestListener;

public class FoursquareAccessTokenRequestListener implements
		AccessTokenRequestListener {
	
	private LoginActivity login;

	public FoursquareAccessTokenRequestListener(LoginActivity parent) {
		setLogin(parent);
	}

	@Override
	public void onError(String errorMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccessGrant(String accessToken) {
		// TODO Auto-generated method stub

	}

	public LoginActivity getLogin() {
		return login;
	}

	public void setLogin(LoginActivity login) {
		this.login = login;
	}

}
