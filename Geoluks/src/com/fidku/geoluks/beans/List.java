package com.fidku.geoluks.beans;

public class List {
	
	private String id;
	private String name;
	private ListCategory category;
	private User user;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ListCategory getCategory() {
		return category;
	}
	public void setCategory(ListCategory category) {
		this.category = category;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
