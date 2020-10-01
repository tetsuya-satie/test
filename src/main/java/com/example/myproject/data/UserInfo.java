package com.example.myproject.data;

public class UserInfo {
	public String no;

	public String id;

	public String authorities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public void addAuthorities(String authority) {
		this.authorities += ("," + authority);
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
}
