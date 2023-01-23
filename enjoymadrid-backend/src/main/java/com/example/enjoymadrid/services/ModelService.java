package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;

@Service
public interface ModelService {

	/**
	 * Get tourist points ranked according to a query
	 * 
	 * @return Ranked tourist points
	 */
	public List<Dictionary> rank();
	
	/**
	 * Get score of a term associated to a document depending of the IR Model used
	 * 
	 * @param dictionaryScoreSpec Input data used for the IR Model
	 * @return Score of IR Model
	 */
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec);
	
}
