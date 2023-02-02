package com.example.enjoymadrid.services;

import java.util.List;

import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.dtos.RouteResultDto;

public interface RouteService {

	/**
	 * Get the routes of a user
	 * 
	 * @param userId ID of user
	 * @return List of routes
	 */
	public List<Route> getUserRoutes(Long userId);
	
	/**
	 * Create a new route and associate it with a user if the user exists
	 * 
	 * @param route Input data to create a route
	 * @param userId ID of user
	 * @return Route
	 */
	public RouteResultDto createRoute(Route route, Long userId);
	
	/**
	 * Delete route from user
	 * 
	 * @param routeId ID of route
	 * @param userId ID of user
	 */
	public void deleteRoute(Long routeId, Long userId);
	
}
