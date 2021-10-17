package com.enjoymadrid.model.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserDto {

	@NotBlank(message = "Name cannot be empty")
	@Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
	private String name;
	
	@NotBlank(message = "Username cannot be empty")
	@Size(max = 50, message = "Username must be less than 50 characters")
	private String username;
	
	private String password;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
