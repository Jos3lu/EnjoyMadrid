package com.example.enjoymadrid.models.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserUpdateDto {
	
	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50, message = "El nombre debe tener menos de 50 caracteres")
	private String name;
	
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
	@Size(max = 50, message = "El nombre de usuario debe tener menos de 50 caracteres")
	private String username;
	
	private String password;
	private String oldPassword;

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
	
	public String getOldPassword() {
		return this.oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
}
