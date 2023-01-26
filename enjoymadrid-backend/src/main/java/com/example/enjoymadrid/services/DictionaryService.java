package com.example.enjoymadrid.services;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;

import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryService {

	/**
	 * Tokenize & filter
	 * 
	 * @param text Text to tokenize and analyze
	 * @param analyzer Analyzer to use
	 * @return List with analyzed tokens
	 */
	public List<String> analyze(String text, Analyzer analyzer);
	
	/**
	 * Word stemming using Snowball algorithm
	 * 
	 * @param term Word to stem
	 * @return Stemmed word
	 */
	public String stem(String term); 
	
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
	public void deleteTouristicPointOfTerm(TouristicPoint point);
	
}
