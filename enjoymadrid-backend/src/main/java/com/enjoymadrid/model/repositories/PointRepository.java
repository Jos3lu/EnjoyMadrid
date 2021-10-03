package com.enjoymadrid.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>{
			
	Point findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
	
}
