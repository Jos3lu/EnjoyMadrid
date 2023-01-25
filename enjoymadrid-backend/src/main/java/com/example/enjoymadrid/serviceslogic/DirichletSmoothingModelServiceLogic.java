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
public class DirichletSmoothingModelServiceLogic implements ModelService {
	
	// Smoothing parameter. In IR literature, DS Model uses a fixed value of µ = 2000
	private final double mu;

	private final DictionaryRepository dictionaryRepository;
	
	@Autowired
	public DirichletSmoothingModelServiceLogic(DictionaryRepository dictionaryRepository) {
		this(2000, dictionaryRepository);
	}
	
	public DirichletSmoothingModelServiceLogic(double mu, DictionaryRepository dictionaryRepository) {
		this.mu = mu;
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
				Double score = scores.getOrDefault(point, Double.valueOf(0.0)) * Math.pow(scorePoint, freq);
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
