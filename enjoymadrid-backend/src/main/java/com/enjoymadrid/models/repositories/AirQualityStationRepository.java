package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.models.AirQualityStation;

public interface AirQualityStationRepository extends JpaRepository<AirQualityStation, Long> {
	
	Optional<AirQualityStation> findByLongitudeAndLatitude(Double longitude, Double latitude);
	
	Optional<AirQualityStation> findByNameIgnoreCase(String name);
	
}