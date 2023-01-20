package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.services.ModelService;

@Service
public class MixedMinAndMaxModelServiceLogic implements ModelService {
	
	// Smoothing parameter in logarithmically scaled tf (term-frequency)
	private final double k;
	
	//private final DictionaryRepository dictionaryRepository;
	
	public MixedMinAndMaxModelServiceLogic() {
		this(0.5);
	}
	
	public MixedMinAndMaxModelServiceLogic(double k) {
		this.k = k;
	}

	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Extended Boolean Model (P-norm)
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param nTermDocs Number of documents where the term T appears
	 * @return Score/weight of term T associated with document D
	 */
	public double calculateScore(int tf, int totalDocs, int nTermDocs) {
		return 0;
	}

}
