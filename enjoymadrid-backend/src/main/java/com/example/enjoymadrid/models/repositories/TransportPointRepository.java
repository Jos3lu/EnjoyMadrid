package com.example.enjoymadrid.models.repositories;

import java.util.Collection;
import java.util.List;

import com.example.enjoymadrid.models.TransportPoint;

public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	List<TransportPoint> findByType(String type);
	
	List<TransportPoint> findByTypeIn(Collection<String> types);
	
	boolean existsByType(String type);
			
}
