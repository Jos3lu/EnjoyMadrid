package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import com.example.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("Touristic")
@Table(name = "TOURISTIC_POINT_TABLE")
public class TouristicPoint extends Point {

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String address;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Integer zipcode;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String phone;

	@Lob
	@Column(columnDefinition = "TEXT")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String description;
	
	@JsonIgnore
	private Integer docLength;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String email;

	@Lob
	@Column(columnDefinition = "TEXT")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String paymentServices;

	@Lob
	@Column(columnDefinition = "TEXT")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String horary;

	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String type;

	@ElementCollection(fetch = FetchType.EAGER)
	@JsonView(TouristicPointInterfaces.BasicData.class)
	@CollectionTable(name = "TOURISTIC_POINT_CATEGORIES_TABLE")
	private List<String> categories = new ArrayList<>();

	@ElementCollection
	@JsonView(TouristicPointInterfaces.BasicData.class)
	@CollectionTable(name = "TOURISTIC_POINT_SUBCATEGORIES_TABLE")
	private List<String> subcategories = new ArrayList<>();

	@ElementCollection
	@Lob
	@Column(columnDefinition = "TEXT")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	@CollectionTable(name = "TOURISTIC_POINT_IMAGES_TABLE")
	private List<String> images = new ArrayList<>();
		
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
	
}
