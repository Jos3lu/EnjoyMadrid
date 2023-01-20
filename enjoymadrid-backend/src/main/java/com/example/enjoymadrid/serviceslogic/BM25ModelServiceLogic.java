package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
public class BM25ModelServiceLogic implements ModelService {
	
	// Free parameter, normally k1 = 1.2 or k1 = 2.0
	private final double k1;
	// Freee parameter, normally b = 0.75
	private final double b;
	// Used in BM25+. In BM25 term frequency normalization by document length is not properly lower-bounded. 
	private final double delta;

	//private final DictionaryRepository dictionaryRepository;
	
	public BM25ModelServiceLogic() {
		this(1.2, 0.75, 1.0);
	}
	
	public BM25ModelServiceLogic(double k1, double b, double delta) {
		if (k1 < 0) {
			throw new IllegalArgumentException("Not valid k1 = " + k1);
		}
		
		if (b < 0 || b > 1) {
			throw new IllegalArgumentException("Not valid b = " + b);
		}
		
		if (delta < 0) {
			throw new IllegalArgumentException("Not valid delta = " + delta);
		}
		
		this.k1 = k1;
		this.b = b;
		this.delta = delta;
	}
	
	@Override
	public List<Dictionary> rank() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param docFreq Number of documents where the term T appears
	 * @param docLength Length of the document D in words
	 * @param avgDoc Average document length in the text collection
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec) {
		// TODO Auto-generated method stub
		return 0;
	}

}
