package com.fidku.geoluks.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class Place {
	public static final String JSON_OBJECT_LABEL = "Place";
	private int id;
	boolean status;
	private String name, type, description;
	private double lat, log, distance;

	public Place(String json) throws JSONException {
		setValues(new JSONObject(json));
	}

	public final void setValues(JSONObject jsonObject) throws JSONException {
		this.id = jsonObject.getInt("id");
		this.status = jsonObject.getBoolean("status");
		this.name = jsonObject.getString("name");
		this.type = jsonObject.getString("type");
		this.description = jsonObject.getString("description");
		this.lat = jsonObject.getDouble("lat");
		this.log = jsonObject.getDouble("log");
		if (jsonObject.has("distance")) {
			this.distance = jsonObject.getDouble("distance");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLog() {
		return log;
	}

	public void setLog(double log) {
		this.log = log;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Place [id=" + id + ", status=" + status + ", name=" + name
				+ ", type=" + type + ", description=" + description + ", lat="
				+ lat + ", log=" + log + ", distance=" + distance + "]";
	}

}
