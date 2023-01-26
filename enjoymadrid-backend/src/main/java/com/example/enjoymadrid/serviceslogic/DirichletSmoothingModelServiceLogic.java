package com.example.enjoymadrid.serviceslogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
@Qualifier("dirichletSmoothingModel")
public class DirichletSmoothingModelServiceLogic implements ModelService {
	
	// Smoothing parameter. In IR literature, DS Model uses a fixed value of µ = 2000
	private final double mu;
	
	@Autowired
	public DirichletSmoothingModelServiceLogic() {
		this(2000);
	}
	
	public DirichletSmoothingModelServiceLogic(double mu) {
		this.mu = mu;
	}
	
	@Override
	public double rank(double score, double scorePoint, int freq) {
		return score * Math.pow(scorePoint, freq);
	}

	/**
	 * Score = (term_frequency + µ * (term_frequency_collection / collection_length) / (doc_length + µ))
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param tfCollection Term frequencies in collection
	 * @param docLength Length of the document D in words
	 * @param collectionLength Length of the collection C in words
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec scoreSpec) {
		return (scoreSpec.getTf() + mu * scoreSpec.getTfCollection() / scoreSpec.getDocLength()) / (scoreSpec.getDocLength() + mu);
	}

}
