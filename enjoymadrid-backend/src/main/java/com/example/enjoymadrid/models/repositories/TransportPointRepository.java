package com.example.enjoymadrid.models.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.enjoymadrid.models.TransportPoint;

@Repository
public interface TransportPointRepository extends PointRepository<TransportPoint> {
	
	List<TransportPoint> findByType(String type);
	
	List<TransportPoint> findByTypeIn(Collection<String> types);
	
	boolean existsByType(String type);
			
}
