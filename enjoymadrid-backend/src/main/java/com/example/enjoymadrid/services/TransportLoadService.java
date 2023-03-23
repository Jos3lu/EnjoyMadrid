package com.example.enjoymadrid.services;

import java.util.List;
import java.util.Map;

import com.example.enjoymadrid.models.TouristicPoint;

public interface TransportLoadService {

	/**
	 * Load Subway, Bus, Commuter & Bicycle points into DB
	 */
	public void loadTransportPoints();
	
	/**
	 * Update information on tourist points near to each transport station
	 * 
	 * @param touristicPoints List<TouristicPoint>
	 */
	public void updateNearbyTouristicPoints(List<TouristicPoint> touristicPoints, Map<String, Long> maxNearbyTouristicPoints);
	
	/**
	 * Update the information related to a Bicycle station
	 */
	public void updateBiciMADPoints();
	
}
