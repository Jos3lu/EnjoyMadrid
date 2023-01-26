package com.example.enjoymadrid.services;

import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryLoadService {

	/**
	 * Load the terms of tourist points associated to its scores into DB
	 * 
	 * @param point Tourist point to extract terms
	 */
	public void loadTerms(TouristicPoint point);
	
	/**
	 * Calculate the scores of the terms associated to its documents
	 */
	public void calculateScoreTerms();
	
}
