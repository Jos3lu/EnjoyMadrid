package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "USER_TABLE")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(UserInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(UserInterfaces.UserData.class)
	@NotBlank(message = "Nombre no puede estar vacío")
	@Size(max = 50, message = "Nombre debe tener menos de 50 caracteres")
	private String name;
	
	@JsonView(UserInterfaces.BasicData.class)
	@NotBlank(message = "Nombre de usuario no puede estar vacío")
	@Size(max = 50, message = "Nombre de usuario debe tener menos de 50 caracteres")
	@Column(unique = true)
	private String username;
	
	@JsonIgnore
	@NotBlank(message = "Contraseña no puede estar vacío")
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", 
		message = "Contraseña debe incluir al menos un número, una minúscula, una mayúscula y al menos 7 caracteres")
	private String password;
	
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	@JsonView(UserInterfaces.PictureData.class)
	private byte[] photo;
		
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "USER_ID")
	@JsonView(UserInterfaces.RouteData.class)
	@CollectionTable(name = "USER_ROUTES")
	private List<Route> routes = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			  name = "USER_TOURISTIC_POINT", 
			  joinColumns = @JoinColumn(name = "USER_ID"), 
			  inverseJoinColumns = @JoinColumn(name = "TOURISTIC_POINT_ID"))
	@JsonView(UserInterfaces.TouristicPointData.class)
	private List<TouristicPoint> touristicPoints = new ArrayList<>();
	
	public User() {}

	public User(
			@NotBlank(message = "Nombre no puede estar vacío") @Size(max = 50, message = "Nombre debe tener menos de 50 caracteres") String name,
			@NotBlank(message = "Nombre de usuario no puede estar vacío") @Size(max = 50, message = "Nombre de usuario debe tener menos de 50 caracteres") String username,
			@NotBlank(message = "Contraseña no puede estar vacío") @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,}", message = "Contraseña debe incluir al menos un número, una minúscula, una mayúscula y al menos 7 caracteres") String password) {
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

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public List<TouristicPoint> getTouristicPoints() {
		return touristicPoints;
	}

	public void setTouristicPoints(List<TouristicPoint> touristicPoints) {
		this.touristicPoints = touristicPoints;
	}
	
}
