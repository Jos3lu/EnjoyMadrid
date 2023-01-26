package com.example.enjoymadrid.services;

import com.example.enjoymadrid.models.DictionaryScoreSpec;

public interface ModelService {

	/**
	 * Add current point score to an accumulated score of a query term
	 * 
	 * @param score Accumulated score of query term
	 * @param scorePoint Point score corresponding to a term
	 * @param freq Frequency of query term
	 * @return Sum of current point score & accumulate score of query term
	 */
	public double rank(double score, double scorePoint, int freq);
	
	/**
	 * Get score of a term associated to a document depending of the IR Model used
	 * 
	 * @param dictionaryScoreSpec Input data used for the IR Model
	 * @return Score of IR Model
	 */
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec);
	
}
