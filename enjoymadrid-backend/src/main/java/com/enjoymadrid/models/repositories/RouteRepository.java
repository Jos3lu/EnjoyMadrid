package com.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.models.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
	Optional<Route> findByNameIgnoreCase(String name);

}
