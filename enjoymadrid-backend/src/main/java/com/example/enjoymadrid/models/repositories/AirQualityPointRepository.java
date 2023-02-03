package com.example.enjoymadrid.models.repositories;

import java.util.List;

import com.example.enjoymadrid.models.AirQualityPoint;

public interface AirQualityPointRepository extends PointRepository<AirQualityPoint> {
	
	List<AirQualityPoint> findByAqiIsNotNull();
		
}