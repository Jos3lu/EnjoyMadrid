package com.example.enjoymadrid.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("AirQuality")
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
