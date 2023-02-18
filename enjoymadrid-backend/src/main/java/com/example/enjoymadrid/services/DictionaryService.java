package com.example.enjoymadrid.services;

import java.util.List;
import java.util.Map;

import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryService {

	/**
	 * Tokenize & filter text
	 * 
	 * @param text Text to tokenize and filter
	 * @return List with tokens
	 */
	public List<String> analyze(String text);
	
	/**
	 * Stem & group the tokens by their frequencies
	 * 
	 * @param terms Tokens
	 * @return Map with tokens stemmed & grouped by frequencies
	 */
	public Map<String, Long> stemAndGetFreq(List<String> terms);
		
	/**
	 * Get tourist points ranked by score
	 * 
	 * @param query Query used to rank
	 * @return Tourist points
	 */
	public List<TouristicPoint> getTouristicPoints(String query);
	
	/**
	 * Remove the score of a term associated to a tourist point
	 * 
	 * @param point Tourist point to remove from scores of term
	 */
	public void deleteTouristicPointFromTerm(TouristicPoint point);
	
}
