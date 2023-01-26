package com.example.enjoymadrid.services;

public interface TransportLoadService {

	/**
	 * Load Subway, Bus, Commuter & Bicycle points into DB
	 */
	public void loadTransportPoints();
	
	/**
	 * Update the information related to a Bicycle station
	 */
	public void updateBiciMADPoints();
	
}
