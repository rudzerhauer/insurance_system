package com.example.model;
import jakarta.persistence.*;
@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String username;
	private String password;
	private String email;
	private String role;
	@Column(name="verification_code")
	private String verificationCode;
	@Column(name="is_verified")
	private boolean isVerified;
	public User() {}
	public User(String username) {
		this.username = username;
	}
	public Long getId() {
		return this.id;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setVerifactionCode(String code) {
		this.verificationCode = code;
	}
	public void setVerified(boolean x) {
		this.isVerified = x;
	}
	public String getEmail() {
		return this.email;
	}
	public String getCode() {
		return this.verificationCode;
	}
	public boolean isVerified() {
		return this.isVerified;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
