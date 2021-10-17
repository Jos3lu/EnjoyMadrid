package com.enjoymadrid.model.dtos;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignInRequestDto {

	@NotBlank(message = "Username cannot be empty")
	@Size(max = 50, message = "Username must be less than 50 characters")
	@Column(unique = true)
	private String username;
	
	@NotBlank(message = "Password cannot be empty")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", 
		message = "Password must have at least one number, one uppercase and one lowercase letter, and at least 7 characters")
	private String password;
	
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
