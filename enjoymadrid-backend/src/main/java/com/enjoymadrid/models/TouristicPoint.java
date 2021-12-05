package com.enjoymadrid.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;

import com.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class TouristicPoint {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Double longitude;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Double latitude;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String name;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String address;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private Integer zipcode;
	
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String phone;
		
	@Lob
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private String description;
	
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
	private List<String> categories = new LinkedList<>();
		
	@ElementCollection
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private List<String> subcategories = new LinkedList<>();
			
	@ElementCollection
	@JsonView(TouristicPointInterfaces.BasicData.class)
	private List<String> images = new LinkedList<>();
	
	public TouristicPoint() {}
	
	public TouristicPoint(Double longitude, Double latitude, @NotEmpty(message = "El nombre no puede estar vacío") String name) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
	}
			
	public TouristicPoint(Double longitude, Double latitude,
			@NotEmpty(message = "El nombre no puede estar vacío") String name, String address, Integer zipcode,
			String phone, String description, String email, String paymentServices, String horary,
			String type, List<String> categories, List<String> subcategories, List<String> images) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
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

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TouristicPoint other = (TouristicPoint) obj;
		return Objects.equals(latitude, other.latitude) && Objects.equals(longitude, other.longitude)
				&& Objects.equals(name, other.name);
	}
	
}
