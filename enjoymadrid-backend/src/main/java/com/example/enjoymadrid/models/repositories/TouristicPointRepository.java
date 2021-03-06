package com.example.enjoymadrid.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.enjoymadrid.models.TouristicPoint;

public interface TouristicPointRepository extends PointRepository<TouristicPoint>{
			
	@Query("SELECT p FROM TouristicPoint p WHERE :category MEMBER p.categories")
	List<TouristicPoint> findByCategory(@Param("category") String category);
	
}
