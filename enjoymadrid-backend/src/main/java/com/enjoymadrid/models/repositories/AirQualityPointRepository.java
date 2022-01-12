package com.enjoymadrid.models.repositories;

import java.util.List;

import com.enjoymadrid.models.AirQualityPoint;

public interface AirQualityPointRepository extends PointRepository<AirQualityPoint> {
	
	List<AirQualityPoint> findByAqiIsNotNull();
		
}