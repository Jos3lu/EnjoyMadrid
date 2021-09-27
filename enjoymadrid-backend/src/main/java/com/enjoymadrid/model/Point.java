package com.enjoymadrid.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.enjoymadrid.model.interfaces.PointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(PointInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(PointInterfaces.BasicData.class)
	private Double longitude;
	
	@JsonView(PointInterfaces.BasicData.class)
	private Double latitude;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String name;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String adress;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String phone;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String description;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String email;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String opening_hours;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String url;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String wheelchair;
	
	@ManyToOne
	@JsonView(PointInterfaces.RouteData.class)
	private Route route;
	
	public Point() {}

	public Point(Double longitude, Double latitude, String name, String adress, String phone,
			String description, String email, String opening_hours, String url, String wheelchair) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
		this.adress = adress;
		this.phone = phone;
		this.description = description;
		this.email = email;
		this.opening_hours = opening_hours;
		this.url = url;
		this.wheelchair = wheelchair;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpening_hours() {
		return opening_hours;
	}

	public void setOpening_hours(String opening_hours) {
		this.opening_hours = opening_hours;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWheelchair() {
		return wheelchair;
	}

	public void setWheelchair(String wheelchair) {
		this.wheelchair = wheelchair;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
}
