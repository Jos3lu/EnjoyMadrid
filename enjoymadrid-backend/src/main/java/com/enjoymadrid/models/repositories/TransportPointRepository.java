package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	@Query("From TransportPoint p where :line member p.lines")
	Optional<TransportPoint> findByLine(@Param("line") String line);
	
}
