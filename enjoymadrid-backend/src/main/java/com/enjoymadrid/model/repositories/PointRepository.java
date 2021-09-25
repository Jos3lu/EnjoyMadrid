package com.enjoymadrid.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>{
	
	Point findByNameIgnoreCase(String name);
	
	Point findByLongitudeAndLatitude(Double longitude, Double latitude);

}
