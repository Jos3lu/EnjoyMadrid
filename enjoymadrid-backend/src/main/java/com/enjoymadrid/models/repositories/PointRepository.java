package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.enjoymadrid.models.Point;

@NoRepositoryBean
public interface PointRepository<T extends Point> extends JpaRepository<T, Long>{

	Optional<T> findByNameIgnoreCase(String name);
	
	Boolean existsByLongitudeAndLatitude(Double longitude, Double latitude);
	
	Optional<T> findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
	
	Boolean existsByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);
		
}
