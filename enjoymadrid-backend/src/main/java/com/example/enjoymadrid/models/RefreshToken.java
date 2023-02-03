package com.example.enjoymadrid.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Column(nullable = false, unique = true)
	private String refreshToken;
	
	@Column(nullable = false)
	private Instant expiryDate;
	
	public RefreshToken() {}

	public RefreshToken(User user, String refreshToken, Instant expiryDate) {
		this.user = user;
		this.refreshToken = refreshToken;
		this.expiryDate = expiryDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}
	
}
