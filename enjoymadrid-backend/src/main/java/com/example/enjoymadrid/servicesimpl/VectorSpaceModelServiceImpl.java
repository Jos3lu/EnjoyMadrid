package com.example.enjoymadrid.servicesimpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
@Qualifier("vectorSpaceModelService")
public class VectorSpaceModelServiceImpl implements ModelService {
		
	public VectorSpaceModelServiceImpl() {}

	@Override
	public double rank(double score, double weight, int freq) {
		return score + (weight * (1 + Math.log10(freq)));
	}
	
	/**
	 * Calculate term weight of a tourist point using the Vector Space Model
	 * Query: (1 + log(term_frequency_query)) * log(total_docs / doc_frequency)
	 * Document: (1 + log(term_frency_doc)) / √∑(1 + log(term_frequency_doc_i))^2
	 * Weight = Query * Document
	 * 
	 * @param tf Frequency of term T in Tourist point P
	 * @param tfSumDoc Sum of the square root of the squared term frequencies (logarithmically scaled) in the Tourist point P
	 * @param totalDocs Total number of tourist points
	 * @param docFreq Number of tourist points where term T appears
	 * @return Weight of Term T associated to Tourist Point P
	 */
	@Override
	public double calculateWeight(TermWeightSpec weightSpec) {
		if (weightSpec.getTf() <= 0) return 0.0;
		
		// Document
		double tfDoc = 1 + Math.log10(weightSpec.getTf()); 
		double cosNormDoc = tfDoc / weightSpec.getTfSumDoc();
		
		// Query: [(1 + log(term_frequency_query)) of query is calculated in rank() function]
		double idfQuery = Math.log10(weightSpec.getTotalDocs() / weightSpec.getDocFreq());
		
		return cosNormDoc * idfQuery;
	}

}
