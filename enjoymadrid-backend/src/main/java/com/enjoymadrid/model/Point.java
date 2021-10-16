package com.enjoymadrid.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

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
	@NotEmpty(message = "Name cannot be empty")
	private String name;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String address;
	
	@JsonView(PointInterfaces.BasicData.class)
	private Integer zipcode;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String phone;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String web;
	
	@Lob
	@JsonView(PointInterfaces.BasicData.class)
	private String description;
	
	@JsonView(PointInterfaces.BasicData.class)
	private String email;
	
	@Lob
	@JsonView(PointInterfaces.BasicData.class)
	private String paymentServices;
	
	@Lob
	@JsonView(PointInterfaces.BasicData.class)
	private String horary;
			
	@JsonView(PointInterfaces.BasicData.class)
	private String type;
	
	@ElementCollection
	@JsonView(PointInterfaces.BasicData.class)
	private List<String> categories = new LinkedList<>();
	
	@ElementCollection
	@JsonView(PointInterfaces.BasicData.class)
	private List<String> subcategories = new LinkedList<>();
			
	@ElementCollection
	@JsonView(PointInterfaces.BasicData.class)
	private List<String> images = new LinkedList<>();
	
	@OneToMany(mappedBy = "point", orphanRemoval = true, cascade = CascadeType.REMOVE)
	@JsonView(PointInterfaces.CommentData.class)
	private List<Comment> comments = new LinkedList<>();
	
	public Point() {}
	
	public Point(Double longitude, Double latitude, String name) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
	}

	public Point(Double longitude, Double latitude, String name, String address, Integer zipcode, String phone,
			String web, String description, String email, String paymentServices, String horary,
			String type, List<String> categories, List<String> subcategories, List<String> images) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
		this.address = address;
		this.zipcode = zipcode;
		this.phone = phone;
		this.web = web;
		this.description = description;
		this.email = email;
		this.paymentServices = paymentServices;
		this.horary = horary;
		this.type = type;
		this.categories = categories;
		this.subcategories = subcategories;
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
	
	public String getPaymentServices() {
		return paymentServices;
	}

	public void setPaymentServices(String paymentServices) {
		this.paymentServices = paymentServices;
	}

	public String getHorary() {
		return horary;
	}

	public void setHorary(String horary) {
		this.horary = horary;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public List<String> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<String> subcategories) {
		this.subcategories = subcategories;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return Objects.equals(latitude, other.latitude) && Objects.equals(longitude, other.longitude)
				&& Objects.equals(name, other.name);
	}
	
}
