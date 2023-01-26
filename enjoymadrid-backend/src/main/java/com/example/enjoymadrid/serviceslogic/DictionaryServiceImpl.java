package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryServiceImpl implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	// Word stemming
	private final SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	private final ModelService modelService;
	private final DictionaryRepository dictionaryRepository;
	
	public DictionaryServiceImpl(
			DictionaryRepository dictionaryRepository,
			@Qualifier("bm25ModelService") ModelService modelService
	) {
		this.dictionaryRepository = dictionaryRepository;
		this.modelService = modelService;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPoints(String query) {
		// Tokenize string, lowercase tokens & stemming
		Map<String, Long> terms = analyze(query, new StandardAnalyzer()).stream()
				.map(term -> stem(term))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); 
		
		// Score of each tourist point
		//ConcurrentHashMap<TouristicPoint, DoubleAdder> scores = new ConcurrentHashMap<>();
		//ConcurrentHashMap<TouristicPoint, DoubleAccumulator> scores = new ConcurrentHashMap<>();
		Map<TouristicPoint, Double> scores = new HashMap<>();
		
		// Iterate over terms of query
		terms.forEach((term, freq) -> {
			Optional<Dictionary> optDict = this.dictionaryRepository.findByTerm(term);
			if (optDict.isEmpty()) return;
			Dictionary dict = optDict.get();
			dict.getWeights().forEach((point, scorePoint) -> {
				// Get accumulative score of query
				double score = scores.getOrDefault(point, Double.valueOf(0.0));
				score = this.modelService.rank(score, scorePoint, freq.intValue());
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
				
		if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
			// Delimit result
		}
		
		return points;
	}
	
	@Override
	public void deleteTouristicPointOfTerm(TouristicPoint point) {
		List<Dictionary> keywords = this.dictionaryRepository.findByWeightsTouristicPoint(point);
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
