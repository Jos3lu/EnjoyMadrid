package com.example.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.PublicTransportLine;

public interface PublicTransportLineRepository extends JpaRepository<PublicTransportLine, Long>{
	
}
