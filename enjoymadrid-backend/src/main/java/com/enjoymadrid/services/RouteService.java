package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.Route;

@Service
public interface RouteService {

	public List<Route> getUserRoutes(Long userId);
	
}
