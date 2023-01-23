package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

@Service
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
