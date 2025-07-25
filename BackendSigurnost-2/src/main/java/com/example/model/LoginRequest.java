package com.example.model;

public class LoginRequest {
	private String username;
	private String password;
	
	public void setUserName(String userName) {
		this.username = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
}
