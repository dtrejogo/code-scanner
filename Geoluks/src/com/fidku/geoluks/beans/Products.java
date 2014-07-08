package com.fidku.geoluks.beans;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class Products {

	private static final String JSON_OBJECT_LABEL = "Product";
	private int id, user_id;
	private int idImagen;
	private String description;
	private String title;
	private String image_url;
	private Bitmap bmProduct;

	private Price price;
	private Place place;
	private String jsonString, add_date, brand, barcode;

	public Products(String jsonString) throws JSONException {
		super();
		this.jsonString = jsonString;
		setValues(new JSONObject(jsonString));
	}

	public void setValues(JSONObject jsonObject) throws JSONException {

		this.id = jsonObject.getInt("id");
		this.add_date = jsonObject.getString("add_date");
		this.title = jsonObject.getString("title");
		this.description = jsonObject.getString("description");
		this.brand = jsonObject.getString("brand");
		this.barcode = jsonObject.getString("barcode");
		this.user_id = jsonObject.getInt("user_id");
		this.image_url = jsonObject.getString("image_url");
		this.price = getPrice();
		this.place = getPlace();

	}

	/**
	 * @return the idProduct
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param idProduct
	 *            the idProduct to set
	 */
	public void setIdProduct(int idProduct) {
		this.id = idProduct;
	}

	/**
	 * @return the productDescription
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param escription
	 *            the productDescription to set
	 */
	public void setDescription(String escription) {
		this.description = escription;
	}

	/**
	 * @return the productName
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the productName to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return image_url;
	}

	public void setImageUrl(String imageUrl) {
		this.image_url = imageUrl;
	}

	public Bitmap getBmProduct() {
		return bmProduct;
	}

	public void setBmProduct(Bitmap bmProduct) {
		this.bmProduct = bmProduct;
	}

	public static ArrayList<Products> getProducts(String products) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(products);
			return getProducts(jsonArray);
		} catch (JSONException e1) {
			System.err.println(e1.getMessage());
			return new ArrayList<Products>();
		}
	}

	public static ArrayList<Products> getProducts(JSONArray jsonArray) {
		ArrayList<Products> listaProductos = new ArrayList<Products>();

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				listaProductos.add(new Products(jsonArray.getJSONObject(i)
						.getString(Products.JSON_OBJECT_LABEL)));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return listaProductos;

	}

	public Price getPrice() {

		if (price == null) {
			try {
				price = new Price(
						new JSONObject(this.jsonString)
								.getString(Price.JSON_OBJECT_LABEL));
			} catch (JSONException e) {
				price = null;
				//e.printStackTrace();
			}
		}
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public Place getPlace() {
		if (place == null) {
			try {
				place = new Place(
						new JSONObject(this.jsonString)
								.getString(Place.JSON_OBJECT_LABEL));
			} catch (JSONException e) {
				place = null;
				//e.printStackTrace();
			}
		}
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public int getIdImagen() {
		return idImagen;
	}

	public void setIdImagen(int idImagen) {
		this.idImagen = idImagen;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getAdd_date() {
		return add_date;
	}

	public void setAdd_date(String add_date) {
		this.add_date = add_date;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
