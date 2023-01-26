package com.example.enjoymadrid.serviceslogic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
@Qualifier("vectorSpaceModelService")
public class VectorSpaceModelServiceLogic implements ModelService {
		
	public VectorSpaceModelServiceLogic() {}

	@Override
	public double rank(double score, double scorePoint, int freq) {
		return score + (scorePoint * (1 + Math.log10(freq)));
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Vector Space Model
	 * Query: (1 + log(term_frequency_query)) * log(total_docs / doc_frequency)
	 * Document: (1 + log(term_frency_doc)) / √∑(1 + log(term_frequency_doc_i))^2
	 * Score = Query * Document
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param docFreq Number of documents where the term T appears
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec scoreSpec) {
		if (scoreSpec.getTf() <= 0) return 0.0;
		
		// Document
		double tfDoc = 1 + Math.log10(scoreSpec.getTf()); 
		double cosNormDoc = tfDoc / scoreSpec.getTfSumDoc();
		
		// Query: [(1 + log(term_frequency_query)) of query is calculated in rank() function]
		double idfQuery = Math.log10(scoreSpec.getTotalDocs() / scoreSpec.getDocFreq());
		
		return cosNormDoc * idfQuery;
	}

}
