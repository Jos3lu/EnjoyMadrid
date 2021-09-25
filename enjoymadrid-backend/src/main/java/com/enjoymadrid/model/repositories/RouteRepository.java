package com.enjoymadrid.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
	Route findByNameIgnoreCase(String name);

}
