package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

@Service
public interface LoadDataAirQualityService {

	public void loadDataAirQualityPoints();
	
	public void updateAqiData();
	
}
