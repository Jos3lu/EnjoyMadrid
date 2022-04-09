package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.models.Route;
import com.enjoymadrid.models.dtos.RouteResultDto;

@Service
public interface RouteService {

	public List<Route> getUserRoutes(Long userId);
		
	public RouteResultDto createRoute(Route route, String username);
	
	public void deleteRoute(Long routeId, Long userId);
	
}
