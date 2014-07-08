package com.fidku.geoluks.beans;

public class Offer {
	
	private String title;
	private String description;
	private String price;
	
	private String uid;
	private String username;
	private String id;
	private String createdDate;
	private String imageUrl;
	private String foursquareId;
	private String foursquareName;
	private String foursquareLatitude;
	private String foursquareLongitude;
	private boolean checked;
	
	
	
	public String getFoursquareId() {
		return foursquareId;
	}

	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}

	public String getFoursquareName() {
		return foursquareName;
	}

	public void setFoursquareName(String foursquareName) {
		this.foursquareName = foursquareName;
	}

	public String getFoursquareLatitude() {
		return foursquareLatitude;
	}

	public void setFoursquareLatitude(String foursquareLatitude) {
		this.foursquareLatitude = foursquareLatitude;
	}

	public String getFoursquareLongitude() {
		return foursquareLongitude;
	}

	public void setFoursquareLongitude(String foursquareLongitude) {
		this.foursquareLongitude = foursquareLongitude;
	}

	
	
	public Offer() {
	}
	
	public Offer(int i, String string) {
		this.id = String.valueOf(i);
		this.title= string;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
