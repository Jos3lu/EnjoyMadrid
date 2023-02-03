package com.example.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
}
