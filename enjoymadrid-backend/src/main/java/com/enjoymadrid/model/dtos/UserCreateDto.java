package com.enjoymadrid.model.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserCreateDto {

	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50, message = "El nombre debe tener menos de 50 caracteres")
	private String name;
	
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
	@Size(max = 50, message = "El nombre de usuario debe tener menos de 50 caracteres")
	private String username;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", 
		message = "La contraseña debe incluir al menos un número, una minúscula, una mayúscula y al menos 7 caracteres")
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
