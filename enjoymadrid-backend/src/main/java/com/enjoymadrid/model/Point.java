package com.enjoymadrid.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

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
	private Integer zipcode;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String phone;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String web;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String description;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String email;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String horary;
			
	@JsonView(PointInterfaces.BasicData.class)
	private String type;
	
	@JsonView(PointInterfaces.BasicData.class)
	private List<String> categories;
	
	@JsonView(PointInterfaces.BasicData.class)
	private List<String> images;
	
	@ManyToMany
	@JsonView(PointInterfaces.RouteData.class)
	private Route route;
	
	public Point() {}

	public Point(Double longitude, Double latitude, String name, String adress, Integer zipcode, String phone, String web,
			String description, String email, String horary, String type, List<String> categories, List<String> images) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
		this.adress = adress;
		this.phone = phone;
		this.web = web;
		this.description = description;
		this.email = email;
		this.horary = horary;
		this.type = type;
		this.categories = categories;
		this.images = images;
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

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
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
		return horary;
	}

	public void setOpening_hours(String horary) {
		this.horary = horary;
	}
	
	public String getHorary() {
		return horary;
	}

	public void setHorary(String horary) {
		this.horary = horary;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
}
