package com.fidku.geoluks.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Price {

	public static final String JSON_OBJECT_LABEL = "Price";
	private int id, product_id, user_id, currency_id, place_id, status;
	private double value;
	private Place place;

	public Price(String json) throws JSONException {
		setValues(new JSONObject(json));
	}

	public final void setValues(JSONObject jsonObject) throws JSONException {
		this.id = jsonObject.getInt("id");
		this.product_id = jsonObject.getInt("product_id");
		this.user_id = jsonObject.getInt("user_id");
		this.currency_id = jsonObject.getInt("currency_id");
		this.place_id = jsonObject.getInt("place_id");
		this.status = jsonObject.getInt("status");
		this.value = jsonObject.getDouble("value");

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(int currency_id) {
		this.currency_id = currency_id;
	}

	public int getPlace_id() {
		return place_id;
	}

	public void setPlace_id(int place_id) {
		this.place_id = place_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public void setPlace(String placeJson) throws JSONException {
		this.place = new Place(placeJson);
	}

	@Override
	public String toString() {
		return "Price [id=" + id + ", product_id=" + product_id + ", user_id="
				+ user_id + ", currency_id=" + currency_id + ", place_id="
				+ place_id + ", status=" + status + ", value=" + value
				+ ", place=" + place + "]";
	}
	

}
