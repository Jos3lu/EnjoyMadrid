package com.example.enjoymadrid.services;

import java.util.List;

import com.example.enjoymadrid.models.TouristicPoint;

public interface TouristicPointService {

	/**
	 * Get tourist points associated to a category
	 * 
	 * @param category Category of tourist points
	 * @return Tourist points
	 */
	public List<TouristicPoint> getTouristicPointsByCategory(String category);
	
	/**
	 * Get tourist points in order of relevance by a means of a query
	 * 
	 * @param query Query to rank tourist points
	 * @return Tourist points
	 */
	public List<TouristicPoint> getTouristicPointsByQuery(String query);
	
	/**
	 * Get the tourist points associated to a user
	 * 
	 * @param userId ID of a user
	 * @return Tourist points
	 */
	public List<TouristicPoint> getUserTouristicPoints(Long userId);
	
	/**
	 * Add a tourist point to a user
	 * 
	 * @param userId ID of a user
	 * @param touristPointId ID of a tourist point
	 */
	public void addTouristicPointToUser(Long userId, Long touristPointId);
	
	/**
	 * Unbundled a tourist point from a user
	 * 
	 * @param userId ID of a user
	 * @param touristPointId ID of a tourist point
	 */
	public void deleteUserTouristicPoint(Long userId, Long touristPointId);
	
}
