package com.example.enjoymadrid.services;

import java.util.List;

import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryLoadService {
	
	/**
	 * Use analyzer to tokenize & filter text
	 * 
	 * @param name Name of point
	 * @param address Address of point
	 * @param zipcode Zipcode of point
	 * @param description Description of point
	 * @return Text split into tokens
	 */
	public List<String> analyzeText(String name, String address, Integer zipcode, String description);
	
	/**
	 * Load the terms of tourist points associated to its scores into DB
	 * 
	 * @param point Tourist point to extract terms
	 */
	public void loadTerms(TouristicPoint point, List<String> resultTerms);
	
	/**
	 * Calculate the scores of the terms associated to its documents
	 */
	public void calculateScoreTerms();
	
}
