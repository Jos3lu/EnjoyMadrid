package com.example.enjoymadrid.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
@Qualifier("dirichletSmoothingModel")
public class DirichletSmoothingModelServiceImpl implements ModelService {
	
	// Smoothing parameter. In IR literature, DS Model uses a fixed value of µ = 2000
	private final double mu;
	
	@Autowired
	public DirichletSmoothingModelServiceImpl() {
		this(2000);
	}
	
	public DirichletSmoothingModelServiceImpl(double mu) {
		this.mu = mu;
	}
	
	@Override
	public double rank(double score, double weight, int freq) {
		return score * Math.pow(weight, freq);
	}

	/**
	 * Weight = (term_frequency + µ * (term_frequency_collection / collection_length) / (doc_length + µ))
	 * 
	 * @param tf Frequency of term T in Tourist point P
	 * @param tfCollection Frequency of Term T in the collection
	 * @param docLength Length of Tourist point P in words
	 * @param collectionLength Length of the collection in words
	 * @return Weight of term T associated with Tourist point P
	 */
	@Override
	public double calculateWeight(TermWeightSpec weightSpec) {
		return (weightSpec.getTf() + mu * weightSpec.getProbTermCol()) / (weightSpec.getDocLength() + mu);
	}

}
