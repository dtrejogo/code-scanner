package com.fidku.geoluks;

import android.app.Application;

public class MyApp extends Application {

    private String latitude;
    private String longitude;
    
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
