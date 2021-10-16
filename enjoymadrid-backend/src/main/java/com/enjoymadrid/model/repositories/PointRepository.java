package com.enjoymadrid.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>{
			
	Optional<Point> findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
	
}
