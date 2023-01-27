package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryServiceImpl implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	// Word stemming
	private final SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	private final ModelService modelService;
	private final DictionaryRepository dictionaryRepository;
	private final TouristicPointRepository touristicPointRepository;
	
	public DictionaryServiceImpl(
			DictionaryRepository dictionaryRepository,
			TouristicPointRepository touristicPointRepository,
			@Qualifier("dirichletSmoothingModel") ModelService modelService) {
		this.dictionaryRepository = dictionaryRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.modelService = modelService;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPoints(String query) {
		// Tokenize string, lowercase tokens & stemming
		Map<String, Long> terms = analyze(query, new StandardAnalyzer()).stream()
				.map(term -> stem(term))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); 
		
		// Score of each tourist point
		Map<TouristicPoint, Double> scores = new HashMap<>();
		//ConcurrentHashMap<TouristicPoint, DoubleAccumulator> scores = new ConcurrentHashMap<>();
		
		// Tourist points (if Dirichlet Smoothing Model used get points from DB)
		List<TouristicPoint> points = new ArrayList<>();
		if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
			points = this.touristicPointRepository.findAll();
		}
		
		// Iterate over terms of query
		//terms.forEach((term, freq) -> {
		for (Entry<String, Long> entry : terms.entrySet()) {
			Optional<Dictionary> optDict = this.dictionaryRepository.findByTerm(entry.getKey());
			if (optDict.isEmpty()) continue;
			
			// Get weights of term associated to the tourist points
			Map<TouristicPoint, Double> weights = optDict.get().getWeights();
			
			// For DS Model (to take account of absent terms)
			if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
				for (TouristicPoint point : points) {
					Double weight = weights.get(point);
					if (weight == null) {
						// Calculate score for absent term
						weight = this.modelService.calculateScore(
								new DictionaryScoreSpec(0, point.getDocLength(), optDict.get().getProbTermCol()));
					}
					// Get accumulative score of query (DS Model)
					calculateQueryScore(scores, point, 1.0, weight, entry.getValue().intValue());
				}
			} 
			// For VS & BM25 Model
			else {
				for (Entry<TouristicPoint, Double> entryPoint : weights.entrySet()) {
					// Get accumulative score of query (VS & BM25 Model)
					calculateQueryScore(scores, entryPoint.getKey(), 0.0, entryPoint.getValue(), entry.getValue().intValue());
				}
			}			
		}
						
		// Order scores
		List<Entry<TouristicPoint, Double>> termEntries = new ArrayList<>(scores.entrySet());
		Collections.sort(termEntries, Collections.reverseOrder(Entry.comparingByValue()));
		// Get only Tourist points
		points = termEntries.stream()
				.map(entry -> entry.getKey())
				.toList();
				
		if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
			// Delimit result
			points = points.subList(0, 100);
		}
		
		return points;
	}
	
	private void calculateQueryScore(Map<TouristicPoint, Double> scores, TouristicPoint point, double initValue, double weight, int freq) {
		double score = scores.getOrDefault(point, initValue);
		score = this.modelService.rank(score, weight, freq);
		scores.put(point, score);
	}
	
	@Override
	public void deleteTouristicPointOfTerm(TouristicPoint point) {
		Set<Dictionary> keywords = this.dictionaryRepository.findByWeightsTouristicPoint(point);
		for (Dictionary dictionary : keywords) {
			Map<TouristicPoint, Double> weights = dictionary.getWeights();
			weights.remove(point);
			this.dictionaryRepository.save(dictionary);
		}
	}

	@Override
	public List<String> analyze(String text, Analyzer analyzer) {
		List<String> result = new ArrayList<>();
		TokenStream tokenStream = analyzer.tokenStream("content", text);
		CharTermAttribute attribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(attribute.toString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public synchronized String stem(String term) {
		spanishStemmer.setCurrent(term);
		spanishStemmer.stem();
		return spanishStemmer.getCurrent();
	}

}
