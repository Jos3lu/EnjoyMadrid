package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface TouristicPointService {

	/**
	 * Get tourist points associated to a category
	 * 
	 * @param category Category of tourist points
	 * @return Tourist points
	 */
	public List<TouristicPoint> getTouristicPointsByCategory(String category);
	
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
