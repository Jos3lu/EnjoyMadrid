package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

@Service
public interface TouristicLoadService {
	
	/**
	 * Load tourist points into DB
	 */
	public void loadTouristicPoints();

}
