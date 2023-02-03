package com.example.enjoymadrid.models.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.enjoymadrid.models.AirQualityPoint;

@Repository
public interface AirQualityPointRepository extends PointRepository<AirQualityPoint> {
	
	List<AirQualityPoint> findByAqiIsNotNull();
		
}