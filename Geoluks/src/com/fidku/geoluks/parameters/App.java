package com.fidku.geoluks.parameters;

public class App {

	public static final String KEY_SESSION_NAME="GEOLUKS_SESSION_KEY";
	public static final String KEY_USER_NAME="GEOLUKS_SESSION_USER_NAME";
	public static final String KEY_USER_UID="GEOLUKS_SESSION_UID";
	public static final String KEY_USER_EMAIL="GEOLUKS_SESSION_EMAIL";
	
	public static final String RADIUS_HOME_FEED="5";
	public static final String HOME_INPUT_FEED = "GEOLUKS_HOME_PRIMARY_FEED";
	public static final String LOCATION_KEY_LATITUDE = "GEOLUKS_LAST_LATITUDE";
	public static final String LOCATION_KEY_LONGITUDE = "GEOLUKS_LAST_LONGITUDE";
	public static final long TIME_AFTER_CONFIRM = 4500;
	public static final String USER_ID_DEFAULT = "1";
	
	
	public static enum SOCIA_NETWORK {	 
		FACEBOOK("Facebook"),
		FOURSQUARE("Foursquare"),
		INSTAGRAM("Instagram"),
		GOOGLE("Google+");

		public String value;

		private SOCIA_NETWORK(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	};
	

}
