package com.enjoymadrid.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(UserInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(UserInterfaces.BasicData.class)
	private String name;
	
	@JsonView(UserInterfaces.BasicData.class)
	private String email;
	
	@JsonView(UserInterfaces.BasicData.class)
	private String password;
	
	@OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.REMOVE)
	@JsonView(UserInterfaces.RoutesData.class)
	private List<Route> routes;
	
	public User() {
		this.routes = new LinkedList<>();
	}

	public User(String name, String email, String password) {
		this();
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

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", routes="
				+ routes + "]";
	}
	
}
