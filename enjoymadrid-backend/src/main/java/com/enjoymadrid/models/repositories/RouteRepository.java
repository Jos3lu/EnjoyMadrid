package com.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.models.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
}
