package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

@Service
public interface AirQualityLoadService {

	public void loadAirQualityPoints();
	
	public void updateAqiData();
	
}
