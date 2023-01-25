package com.example.enjoymadrid.serviceslogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.ModelService;

@Service
public class BM25ModelServiceLogic implements ModelService {
	
	// Free parameter, normally k1 = [1.2,2.0]
	private final double k1;
	// Free parameter, normally b = 0.75
	private final double b;
	// Used in BM25+. In BM25 term frequency normalization by document length is not properly lower-bounded. 
	// Minimizing the chances of over-penalizing those very long documents
	// Normally delta = 1
	private final double delta;

	private final DictionaryRepository dictionaryRepository;
	
	@Autowired
	public BM25ModelServiceLogic(DictionaryRepository dictionaryRepository) {
		this(1.2, 0.75, 1.0, dictionaryRepository);
	}
	
	public BM25ModelServiceLogic(double k1, double b, double delta, DictionaryRepository dictionaryRepository) {
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
		this.dictionaryRepository = dictionaryRepository;
	}
	
	@Override
	public List<TouristicPoint> rank(Map<String, Long> terms) {
		// Score of each tourist point
		//ConcurrentHashMap<TouristicPoint, DoubleAdder> scores = new ConcurrentHashMap<>();
		Map<TouristicPoint, Double> scores = new HashMap<>();
		
		// Iterate over terms of query
		terms.forEach((term, freq) -> {
			Optional<Dictionary> optDict = this.dictionaryRepository.findByTerm(term);
			if (optDict.isEmpty()) return;
			Dictionary dict = optDict.get();
			dict.getWeights().forEach((point, scorePoint) -> {
				// Get accumulative score of point
				// scores.computeIfAbsent(point, v -> new DoubleAdder()).add(scorePoint * freq);
				Double score = scores.getOrDefault(point, Double.valueOf(0.0)) + (scorePoint * freq);
				scores.put(point, score);
			});
		});
				
		// Order scores
		List<Entry<TouristicPoint, Double>> termEntries = new ArrayList<>(scores.entrySet());
		Collections.sort(termEntries, Collections.reverseOrder(Entry.comparingByValue()));
		// Get only Tourist points
		List<TouristicPoint> points = termEntries.stream()
				.map(entry -> entry.getKey())
				.toList();
		
		return points;
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
