package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.BycicleTransportPoint;
import com.enjoymadrid.models.PublicTransportPoint;
import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	@Query("Select PT from PublicTransportPoint PT where :line member PT.lines")
	Optional<PublicTransportPoint> findByLine(@Param("line") String line);
	
	Optional<BycicleTransportPoint> findByStationNumber(String stationNumber);
	
	@Query("Select count(BT) > 0 from BycicleTransportPoint BT where :stationNumber = BT.stationNumber")
	Boolean existsByStationNumber(@Param("stationNumber") String stationNumber);
	
}
