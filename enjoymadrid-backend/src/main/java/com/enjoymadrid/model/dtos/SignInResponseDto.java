package com.enjoymadrid.model.dtos;

public class SignInResponseDto {
	
	private String token;
	private Long id;
	private String name;
	private String username;
	private byte[] photo;
	
	public SignInResponseDto() {}
	
	public SignInResponseDto(String token, Long id, String name, String username, byte[] photo) {
		this.token = token;
		this.id = id;
		this.name = name;
		this.username = username;
		this.photo = photo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
}
