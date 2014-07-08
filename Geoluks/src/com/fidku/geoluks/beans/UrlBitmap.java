package com.fidku.geoluks.beans;

import android.graphics.Bitmap;

public class UrlBitmap{
	String UrlImage;
	Bitmap imagen;
	public UrlBitmap(String urlImage, Bitmap imagen) {
		super();
		UrlImage = urlImage;
		this.imagen = imagen;
	}
	public String getUrlImage() {
		return UrlImage;
	}
	public void setUrlImage(String urlImage) {
		UrlImage = urlImage;
	}
	public Bitmap getImagen() {
		return imagen;
	}
	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}
	
	
}