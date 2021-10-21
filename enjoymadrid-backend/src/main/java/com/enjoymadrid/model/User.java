package com.enjoymadrid.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(UserInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(UserInterfaces.BasicData.class)
	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50, message = "El nombre debe tener menos de 50 caracteres")
	private String name;
	
	@JsonView(UserInterfaces.UsernameData.class)
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
	@Size(max = 50, message = "El nombre de usuario debe tener menos de 50 caracteres")
	@Column(unique = true)
	private String username;
	
	@JsonIgnore
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", 
		message = "La contraseña debe incluir al menos un número, una minúscula, una mayúscula y al menos 7 caracteres")
	private String password;
	
	@Lob
	@JsonView(UserInterfaces.PictureData.class)
	private byte[] photo;
	
	@OneToMany(mappedBy = "point", orphanRemoval = true, cascade = CascadeType.REMOVE)
	@JsonView(UserInterfaces.CommentData.class)
	private List<Comment> comments = new LinkedList<>();
	
	@OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonView(UserInterfaces.RouteData.class)
	private List<Route> routes = new LinkedList<>();;
	
	public User() {}

	public User(String name, String username, String password) {
		this.name = name;
		this.username = username;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
}
