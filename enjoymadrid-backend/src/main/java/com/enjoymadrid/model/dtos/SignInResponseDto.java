package com.enjoymadrid.model.dtos;

public class SignInResponseDto {
	
	private String token;
	
	public SignInResponseDto() {}
	
	public SignInResponseDto(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
}
