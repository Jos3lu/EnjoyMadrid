package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.models.Route;

@Service
public interface RouteService {

	public List<Route> getUserRoutes(String username);
		
	public Route createRoute(Route route, String username);
	
}
