package com.example.enjoymadrid.models.dtos;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDto {

	@NotBlank(message = "Refresh token no puede estar vacío")
	private String refreshToken;
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
