package com.example.enjoymadrid.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("AirQuality")
@Table(name = "AIR_QUALITY_POINT_TABLE")
public class AirQualityPoint extends Point {

	private Integer aqi;

	public AirQualityPoint() {}
	
	public AirQualityPoint(String name, Double longitude, Double latitude) {
		super(name, longitude, latitude);
	}

	public Integer getAqi() {
		return aqi;
	}

	public void setAqi(Integer aqi) {
		this.aqi = aqi;
	}
		
}
