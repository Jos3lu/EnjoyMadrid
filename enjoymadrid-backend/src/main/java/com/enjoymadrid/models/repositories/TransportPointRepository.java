package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends JpaRepository<TransportPoint, Long> {
	
	Optional<TransportPoint> findTopByNameIgnoreCaseAndLongitudeAndLatitude(String name, Double longitude, Double latitude);

	@Query("From TransportPoint p where :line member p.lines")
	Optional<TransportPoint> findByLine(@Param("line") String category);
	
}
