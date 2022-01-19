package com.enjoymadrid.models.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enjoymadrid.models.BicycleTransportPoint;
import com.enjoymadrid.models.PublicTransportPoint;
import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	List<TransportPoint> findByTypeIn(Collection<String> types);
	
	@Query("SELECT pt FROM PublicTransportPoint pt WHERE :line MEMBER pt.lines")
	Optional<PublicTransportPoint> findByLine(@Param("line") String line);
	
	Optional<BicycleTransportPoint> findByStationNumber(String stationNumber);
	
	Boolean existsByNameIgnoreCaseAndType(String name, String type);
	
	@Query("SELECT COUNT(bt) > 0 FROM BicycleTransportPoint bt WHERE :stationNumber = bt.stationNumber")
	Boolean existsByStationNumber(@Param("stationNumber") String stationNumber);
	
}
