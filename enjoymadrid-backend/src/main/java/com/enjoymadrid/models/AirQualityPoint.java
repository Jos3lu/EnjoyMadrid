package com.enjoymadrid.models;

import javax.persistence.Entity;

@Entity
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
