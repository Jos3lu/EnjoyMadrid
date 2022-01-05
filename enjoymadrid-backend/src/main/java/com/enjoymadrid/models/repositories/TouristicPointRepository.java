package com.enjoymadrid.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.TouristicPoint;

public interface TouristicPointRepository extends PointRepository<TouristicPoint>{
			
	@Query("SELECT p FROM TouristicPoint p WHERE :category MEMBER p.categories")
	Optional<List<TouristicPoint>> findByCategory(@Param("category") String category);

	/*
	// Haversine Formula
	String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) *" +
	        " cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";
	
	@Query("SELECT p FROM TouristicPoint p WHERE " + HAVERSINE_FORMULA + " < :distance ORDER BY " + HAVERSINE_FORMULA + " DESC")
	List<TouristicPoint> findTouristicPointsWithInDistance(@Param("longitude") Double longitude, 
			@Param("latitude") Double latitude, @Param("distance") Double distance);
	*/
}
