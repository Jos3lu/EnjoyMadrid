package com.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.models.PublicTransportLine;

public interface PublicTransportLineRepository extends JpaRepository<PublicTransportLine, Long>{
	
}
