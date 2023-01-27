package com.example.enjoymadrid.serviceslogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.DictionaryScoreSpec;
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
	 * Score = tf * idf
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param docFreq Number of documents where the term T appears
	 * @param docLength Length of the document D in words
	 * @param avgDoc Average document length in the text collection
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec scoreSpec) {
		if (scoreSpec.getTf() <= 0) return 0.0;
		
		double tf = scoreSpec.getTf() * (k1 + 1)
				/ (scoreSpec.getTf() + k1 * (1 - b + b * scoreSpec.getDocLength() / scoreSpec.getAvgDoc()));
		double idf = Math.log10((scoreSpec.getTotalDocs() - scoreSpec.getDocFreq() + 0.5) / (scoreSpec.getDocFreq() + 0.5) + 1);
		
		return (tf + delta) * idf;
	}

}
