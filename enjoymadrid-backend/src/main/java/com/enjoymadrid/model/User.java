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
import javax.validation.constraints.Email;
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
	@NotBlank(message = "Name cannot be empty")
	@Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
	private String name;
	
	@JsonView(UserInterfaces.EmailData.class)
	@Email(message = "Email must be valid")
	@NotBlank(message = "Email cannot be empty")
	@Column(unique = true)
	private String email;
	
	@JsonIgnore
	@NotBlank(message = "Password cannot be empty")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", 
		message = "Password must be at least one number, one uppercase and one lowercase letter, and at least 7 characters")
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

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
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
