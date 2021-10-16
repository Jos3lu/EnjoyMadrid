package com.enjoymadrid.model.dtos;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class LoginRequestDto {

	@NotBlank(message = "Name cannot be empty")
	@Email(message = "Email must be valid")
	@NotBlank(message = "Email cannot be empty")
	@Column(unique = true)
	private String email;
	
	@NotBlank(message = "Password cannot be empty")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", message = "Password must be at least one number, one uppercase and one lowercase letter, and at least 7 characters")
	private String password;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}
