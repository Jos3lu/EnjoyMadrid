package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.models.PublicTransportLine;

public interface PublicTransportLineRepository extends JpaRepository<PublicTransportLine, Long>{

	Optional<PublicTransportLine> findByLineAndDirection(String line, String direction);
	
}
