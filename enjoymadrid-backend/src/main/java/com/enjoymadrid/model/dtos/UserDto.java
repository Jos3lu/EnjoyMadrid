package com.enjoymadrid.model.dtos;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserDto {

	@NotBlank(message = "Name cannot be empty")
	@Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
	private String name;
	
	@Email(message = "Email must be valid")
	@NotBlank(message = "Email cannot be empty")
	@Column(unique = true)
	private String email;
	
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
