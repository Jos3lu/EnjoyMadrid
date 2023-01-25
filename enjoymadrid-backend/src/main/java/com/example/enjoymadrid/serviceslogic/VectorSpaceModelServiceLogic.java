package com.example.enjoymadrid.serviceslogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.ModelService;

@Service
public class VectorSpaceModelServiceLogic implements ModelService {
	
	private final DictionaryRepository dictionaryRepository;
	
	public VectorSpaceModelServiceLogic(DictionaryRepository dictionaryRepository) {
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
				// scores.computeIfAbsent(point, v -> new DoubleAdder()).add(scorePoint * (1 + Math.log10(freq)));
				Double score = scores.getOrDefault(point, Double.valueOf(0.0)) + (scorePoint * (1 + Math.log10(freq)));
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
