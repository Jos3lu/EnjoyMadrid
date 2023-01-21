package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
public class MixedMinAndMaxModelServiceLogic implements ModelService {
	
	// Softness coefficients for the OR operator
	private final double c_or1;
	private final double c_or2;
	
	// Softness coefficients for the AND operator
	private final double c_and1;
	private final double c_and2;
	 
	//private final DictionaryRepository dictionaryRepository;
	
	public MixedMinAndMaxModelServiceLogic() {
		this(0.6, 0.4, 0.6, 0.4);
	}
	
	public MixedMinAndMaxModelServiceLogic(double c_or1, double c_or2, double c_and1, double c_and2) {
		this.c_or1 = 0;
		this.c_or2 = 0;
		this.c_and1 = 0;
		this.c_and2 = 0;
	}
	
	@Override
	public List<Dictionary> rank() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Extended Boolean Model (P-norm)
	 * tf = 1 + log(term_frequency)
	 * idf = log(total_docs / doc_frequency)
	 * Score = tf * idf
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param docFreq Number of documents where the term T appears
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec scoreSpec) {
		if (scoreSpec.getTf() <= 0) return 0.0;
		
		double tf = 1 + Math.log10(scoreSpec.getTf());
		double idf = Math.log10(scoreSpec.getTotalDocs() / scoreSpec.getDocFreq());
		
		return tf * idf;
	}

}
