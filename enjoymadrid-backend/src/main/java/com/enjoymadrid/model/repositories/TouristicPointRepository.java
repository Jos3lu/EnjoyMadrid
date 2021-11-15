package com.enjoymadrid.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.model.TouristicPoint;

public interface TouristicPointRepository extends JpaRepository<TouristicPoint, Long>{
			
	Optional<TouristicPoint> findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
	
	@Query("From TouristicPoint p where :category member p.categories")
	Optional<List<TouristicPoint>> findByCategory(@Param("category") String category);
	
}
