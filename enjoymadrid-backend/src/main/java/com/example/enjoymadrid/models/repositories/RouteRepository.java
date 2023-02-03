package com.example.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enjoymadrid.models.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
	
}
