package com.enjoymadrid.models.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.BycicleTransportPoint;
import com.enjoymadrid.models.PublicTransportPoint;
import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	List<TransportPoint> findByTypeIn(Collection<String> types);
	
	@Query("SELECT pt FROM PublicTransportPoint pt WHERE :line MEMBER pt.lines")
	Optional<PublicTransportPoint> findByLine(@Param("line") String line);
	
	Optional<BycicleTransportPoint> findByStationNumber(String stationNumber);
	
	@Query("SELECT COUNT(bt) > 0 FROM BycicleTransportPoint bt WHERE :stationNumber = bt.stationNumber")
	Boolean existsByStationNumber(@Param("stationNumber") String stationNumber);
	
	// Haversine Formula
	String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) *" +
	        " cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";
	
	@Query("SELECT p FROM TransportPoint p WHERE " + HAVERSINE_FORMULA + " < :distance ORDER BY " + HAVERSINE_FORMULA + " DESC")
	List<TransportPoint> findTransportPointsWithInDistance(@Param("longitude") Double longitude, 
			@Param("latitude") Double latitude, @Param("distance") Double distance);
		
}
