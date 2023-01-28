package com.example.enjoymadrid.services;

import java.util.List;

import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryLoadService {
	
	/**
	 * Tokenize & filter text
	 * 
	 * @param point Point from which the text is extracted
	 * @return Text split into tokens
	 */
	public List<String> analyzeText(TouristicPoint point);
	
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
