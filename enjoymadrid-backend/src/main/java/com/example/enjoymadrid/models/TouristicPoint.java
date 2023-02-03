package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;

import com.example.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@DiscriminatorValue("Touristic")
public class TouristicPoint extends Point {

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String address;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Integer zipcode;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String phone;

	@Lob
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String description;
	
	@JsonIgnore
	private Integer docLength;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String email;

	@Lob
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String paymentServices;

	@Lob
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String horary;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String type;

	@ElementCollection
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private List<String> categories = new ArrayList<>();

	@ElementCollection
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private List<String> subcategories = new ArrayList<>();

	@ElementCollection
	@Column(columnDefinition = "LONGTEXT")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private List<String> images = new ArrayList<>();
	
	@JsonIgnore
	@ManyToMany(mappedBy = "touristicPoints")
	private List<User> users = new ArrayList<>();
	
	public TouristicPoint() {}

	public TouristicPoint(String name, Double longitude, Double latitude) {
		super(name, longitude, latitude);
	}

	public TouristicPoint(String name, Double longitude, Double latitude, String address, Integer zipcode, String phone,
			String description, String email, String paymentServices, String horary, String type,
			List<String> categories, List<String> subcategories, List<String> images) {
		super(name, longitude, latitude);
		this.address = address;
		this.zipcode = zipcode;
		this.phone = phone;
		this.description = description;
		this.email = email;
		this.paymentServices = paymentServices;
		this.horary = horary;
		this.type = type;
		this.categories = categories;
		this.subcategories = subcategories;
		this.images = images;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDocLength() {
		return docLength;
	}

	public void setDocLength(Integer docLength) {
		this.docLength = docLength;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
