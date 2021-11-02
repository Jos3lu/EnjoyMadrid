package com.enjoymadrid.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.TouristicPoint;

public interface TouristicPointRepository extends JpaRepository<TouristicPoint, Long>{
			
	Optional<TouristicPoint> findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
	
}
