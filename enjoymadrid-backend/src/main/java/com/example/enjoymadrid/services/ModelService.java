package com.example.enjoymadrid.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface ModelService {

	/**
	 * Get tourist points ranked according to a query
	 * 
	 * @return Ranked tourist points
	 */
	public List<TouristicPoint> rank(Map<String, Long> terms);
	
	/**
	 * Get score of a term associated to a document depending of the IR Model used
	 * 
	 * @param dictionaryScoreSpec Input data used for the IR Model
	 * @return Score of IR Model
	 */
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec);
	
}
