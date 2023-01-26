package com.example.enjoymadrid.services;

public interface AirQualityLoadService {

	/**
	 * Load Air quality stations from file into DB
	 */
	public void loadAirQualityPoints();
	
	/**
	 * Use Web scraping & API to load/update AQI of air quality stations
	 */
	public void updateAqiData();
	
}
