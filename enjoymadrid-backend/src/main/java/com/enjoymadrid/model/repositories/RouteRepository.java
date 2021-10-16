package com.enjoymadrid.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
	Optional<Route> findByNameIgnoreCase(String name);

}
