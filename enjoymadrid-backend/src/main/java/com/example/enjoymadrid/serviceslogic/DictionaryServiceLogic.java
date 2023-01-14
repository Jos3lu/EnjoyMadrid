package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryService;

@Service
public class DictionaryServiceLogic implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	// Word stemming
	private static final SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	private DictionaryRepository dictionaryRepository;
	
	public DictionaryServiceLogic(DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
	}
	
	@Override
	public void deleteTouristicPointOfTerm(Dictionary term, TouristicPoint point) {
		term.getWeights().remove(point);
		this.dictionaryRepository.save(term);
	}

	/**
	 * Tokenize & filter
	 * 
	 * @param text Text to tokenize and analyze
	 * @param analyzer Analyzer to use
	 * @return List with analyzed tokens
	 */
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

	/**
	 * Word stemming using Snowball algorithm
	 * 
	 * @param term Word to stem
	 * @return Stemmed word
	 */
	@Override
	public synchronized String stem(String term) {
		spanishStemmer.setCurrent(term);
		spanishStemmer.stem();
		return spanishStemmer.getCurrent();
	}

}