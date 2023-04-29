package com.example.enjoymadrid.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
@Qualifier("bm25ModelService")
public class BM25ModelServiceImpl implements ModelService {
	
	// Free parameter, normally k1 = [1.2,2.0]
	private final double k1;
	// Free parameter, normally b = 0.75
	private final double b;
	// Used in BM25+. In BM25 term frequency normalization by document length is not properly lower-bounded. 
	// Minimizing the chances of over-penalizing those very long documents
	// Normally delta = 1
	private final double delta;
	
	@Autowired
	public BM25ModelServiceImpl() {
		this(1.2, 0.75, 1.0);
	}
	
	public BM25ModelServiceImpl(double k1, double b, double delta) {
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
	public double rank(double score, double weight, int freq) {
		return score + (weight * freq);
	}

	/**
	 * tf = term_frequency * (k1 + 1) / (term_frequency + k1 * (1 - b + b * document_length / average_document_length))
	 * idf = log((total_docs - doc_frequency) / (doc_frequency  + 0.5) + 1)
	 * Weight = tf * idf
	 * 
	 * @param tf Frequency of term T in Tourist point P
	 * @param totalDocs Total number of tourist points
	 * @param docFreq Number of tourist points where term T appears
	 * @param docLength Length of Tourist point P in words
	 * @param avgDoc Average Tourist point length in the text collection
	 * @return Weight of term T associated with Tourist point P
	 */
	@Override
	public double calculateWeight(TermWeightSpec weightSpec) {
		if (weightSpec.getTf() <= 0) return 0.0;
		
		double tf = weightSpec.getTf() * (k1 + 1)
				/ (weightSpec.getTf() + k1 * (1 - b + b * weightSpec.getDocLength() / weightSpec.getAvgDoc()));
		double idf = Math.log10((weightSpec.getTotalDocs() - weightSpec.getDocFreq() + 0.5) / (weightSpec.getDocFreq() + 0.5) + 1);
		
		return (tf + delta) * idf;
	}

}
