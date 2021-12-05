package com.enjoymadrid.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class AirQualityStation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "El nombre no puede estar vacío")
	private String name;

	private Integer aqi;

	private Double longitude;

	private Double latitude;

	public AirQualityStation() {
	}
	
	public AirQualityStation(@NotBlank(message = "El nombre no puede estar vacío") String name, Integer aqi) {
		this.name = name;
		this.aqi = aqi;
	}

	public AirQualityStation(@NotBlank(message = "El nombre no puede estar vacío") String name, Double longitude,
			Double latitude) {
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
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

	public Integer getIqa() {
		return aqi;
	}

	public void setIqa(Integer aqi) {
		this.aqi = aqi;
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

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AirQualityStation other = (AirQualityStation) obj;
		return name.equalsIgnoreCase(other.name);
	}
		
}
