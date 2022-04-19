package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.dtos.RouteResultDto;

@Service
public interface RouteService {

	public List<Route> getUserRoutes(Long userId);
		
	public RouteResultDto createRoute(Route route, Long userId);
	
	public void deleteRoute(Long routeId, Long userId);
	
}
