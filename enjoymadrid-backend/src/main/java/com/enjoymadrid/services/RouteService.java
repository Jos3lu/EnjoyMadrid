package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.models.Route;
import com.enjoymadrid.models.dtos.RouteResponseDto;

@Service
public interface RouteService {

	public List<Route> getUserRoutes(String username);
		
	public RouteResponseDto createRoute(Route route, String username);
	
	public void deleteRoute(Long routeId, String username);
	
}
