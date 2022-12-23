package com.example.enjoymadrid.models.dtos;

import java.util.List;

import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.TouristicPoint;

public class SignInResponseDto {
	
	private String token;
	private String refreshToken;
	private Long id;
	private String name;
	private String username;
	private byte[] photo;
	private List<Route> routes;
	private List<TouristicPoint> touristicPoints;
		
	public SignInResponseDto(String token, String refreshToken, Long id, String name, String username, byte[] photo,
			List<Route> routes, List<TouristicPoint> touristicPoints) {
		this.token = token;
		this.refreshToken = refreshToken;
		this.id = id;
		this.name = name;
		this.username = username;
		this.photo = photo;
		this.routes = routes;
		this.touristicPoints = touristicPoints;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
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
