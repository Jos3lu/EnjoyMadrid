package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryServiceLogic implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	// Word stemming
	private final SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	private final ModelService modelService;
	private final DictionaryRepository dictionaryRepository;
	
	public DictionaryServiceLogic(
			DictionaryRepository dictionaryRepository,
			VectorSpaceModelServiceLogic vectorSpaceModelServiceLogic, 
			BM25ModelServiceLogic bm25ModelServiceLogic,
			DirichletSmoothingModelServiceLogic dirichletSmoothingModelServiceLogic
	) {
		this.dictionaryRepository = dictionaryRepository;
		this.modelService = vectorSpaceModelServiceLogic;
//		this.modelService = bm25ModelServiceLogic;
//		this.modelService = dirichletSmoothingModelServiceLogic;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPoints(String query) {
		// Tokenize string, lowercase tokens & stemming
		Map<String, Long> terms = analyze(query, new StandardAnalyzer()).stream()
				.map(term -> stem(term))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); 
		return this.modelService.rank(terms);
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
