package com.enjoymadrid.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.TouristicPoint;

public interface TouristicPointRepository extends PointRepository<TouristicPoint>{
			
	@Query("Select P From TouristicPoint P where :category member P.categories")
	Optional<List<TouristicPoint>> findByCategory(@Param("category") String category);
	
}
