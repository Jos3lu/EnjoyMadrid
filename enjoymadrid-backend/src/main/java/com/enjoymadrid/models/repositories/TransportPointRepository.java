package com.enjoymadrid.models.repositories;

import java.util.Collection;
import java.util.List;

import com.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	List<TransportPoint> findByType(String type);
	
	List<TransportPoint> findByTypeIn(Collection<String> types);
	
	//@Query("SELECT pt FROM PublicTransportPoint pt WHERE :line MEMBER pt.lines")
	//Optional<PublicTransportPoint> findByLine(@Param("line") String line);
	
	//Optional<BicycleTransportPoint> findByStationNumber(String stationNumber);
		
	//@Query("SELECT COUNT(bt) > 0 FROM BicycleTransportPoint bt WHERE :stationNumber = bt.stationNumber")
	//Boolean existsByStationNumber(@Param("stationNumber") String stationNumber);
	
}
