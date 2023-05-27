package com.example.enjoymadrid.servicesimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TermWeight;
import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryLoadService;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryLoadServiceImpl implements DictionaryLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoadService.class);
	
	// Term frequencies
	private ConcurrentHashMap<String, ConcurrentHashMap<TouristicPoint, Integer>> termFreq = new ConcurrentHashMap<>();
	// Square root of the sum of the squared term frequencies in a document D 
	private ConcurrentHashMap<TouristicPoint, Double> tfSumDoc = new ConcurrentHashMap<>();
	// Term frequencies in collection
	private ConcurrentHashMap<String, LongAdder> termFreqCollection = new ConcurrentHashMap<>();
	// Total number of documents/touristic points
	private LongAdder totalDocs = new LongAdder();
	// Number of documents where the term T appears
	private ConcurrentHashMap<String, LongAdder> docFreq = new ConcurrentHashMap<>();
	// Length of the document D in words
	private ConcurrentHashMap<TouristicPoint, Integer> docsLength = new ConcurrentHashMap<>();
	// Length of the collection C in words
	private LongAdder collectionLength = new LongAdder();
	
	// Dependency injection
	private final DictionaryRepository dictionaryRepository;
	private final ModelService modelService;
	private final DictionaryService dictionaryService;

	public DictionaryLoadServiceImpl(DictionaryRepository dictionaryRepository, DictionaryService dictionaryService,
			@Qualifier("bm25ModelService") ModelService modelService) {
		this.dictionaryRepository = dictionaryRepository;
		this.dictionaryService = dictionaryService;
		this.modelService = modelService;
	}

	@Override
	public List<String> analyzeText(TouristicPoint point) {
		// Get title & description from point
		StringJoiner text = new StringJoiner(" ");
		text.add(getStringIfNotNull(point.getName()));
		text.add(getStringIfNotNull(parseHtml(point.getDescription())));
		
		// Tokenize string, lowercase tokens, filter symbols/stop words
		return this.dictionaryService.analyze(text.toString()); 
	}

	@Override
	public void loadTerms(TouristicPoint point, List<String> terms) {
		// Stemming then group by frequency in Map
		Map<String, Long> termFreqDocs = this.dictionaryService.stemAndGetFreq(terms);
			
		int docLength = terms.size();
		// Doc -> doc length
		docsLength.put(point, docLength);
		// Add terms count to collection
		collectionLength.add(docLength);
		// Add doc to total
		totalDocs.increment();
		
		// Sum of the squared frequency terms (logarithmically scaled) in a document D 
		double tfSum = 0.0;
		
		for (Map.Entry<String, Long> entry : termFreqDocs.entrySet()) {
			// Term -> (Document -> Frequency)
			termFreq.computeIfAbsent(entry.getKey(), v -> new ConcurrentHashMap<TouristicPoint, Integer>())
				.put(point, entry.getValue().intValue());
			// Term -> Frequency collection
			termFreqCollection.computeIfAbsent(entry.getKey(), v -> new LongAdder()).add(entry.getValue().longValue());
			// Increase occurrences of the term in documents
			docFreq.computeIfAbsent(entry.getKey(), v -> new LongAdder()).increment();
			// Add squared tf (logarithmically scaled) for each term T in the document D
			tfSum += Math.pow(1 + Math.log10(entry.getValue().intValue()), 2);
		}
		// Set square root of tfSum into document/point
		tfSumDoc.put(point, Math.sqrt(tfSum));
	}
	
	@Override
	public void calculateScoreTerms() {	
		// Remove all the entities
		this.dictionaryRepository.deleteAll();
		// Iterate over: terms -> (Tourist points -> frequency) to calculate weights
		termFreq.entrySet().parallelStream().forEach(entryTerm -> {
			String term = entryTerm.getKey();
			// Map for weights
			Map<TouristicPoint, Double> weights = new HashMap<>();
			for (Entry<TouristicPoint, Integer> entryPoint: entryTerm.getValue().entrySet()) {
				// Get data to calculate weight
				TouristicPoint touristicPoint = entryPoint.getKey();
				int tf = entryPoint.getValue().intValue();
				
				// Vector Space Model
//				TermWeightSpec weightSpec = new TermWeightSpec(tf, totalDocs.intValue(),
//						docFreq.get(term).intValue(), tfSumDoc.get(touristicPoint));
				
				// BM25 Model
				TermWeightSpec weightSpec = new TermWeightSpec(tf, totalDocs.intValue(), docFreq.get(term).intValue(), 
						docsLength.get(touristicPoint).intValue(), ((double) collectionLength.longValue()) / totalDocs.intValue());
				
				// Dirichlet Smoothing Model
//				TermWeightSpec weightSpec = new TermWeightSpec(tf, docsLength.get(touristicPoint).intValue(),
//						((double) termFreqCollection.get(term).intValue()) / collectionLength.longValue());
								
				// Model to use for documents score
				double weight = this.modelService.calculateWeight(weightSpec);
								
				// Don't store a score = 0
				if (weight == 0) continue;
				// Save the score associated to the tourist point (document)
				weights.put(touristicPoint, weight);
			}
			// Create term -> weights
			TermWeight termWeight = new TermWeight(term, weights);
			if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) 
				termWeight.setProbTermCol(((double) termFreqCollection.get(term).intValue()) / collectionLength.longValue());
			
			// Save the term & scores in DB
			this.dictionaryRepository.save(termWeight);
		});
		
		//Reset the variables for the next time the function is called
		resetVariables();
						
		logger.info("Terms from descriptions of tourist points updated");
	}
				
	/**
	 * Parse html into normalized, combined text
	 * 
	 * @param html Html to normalized text
	 * @return Normalized text
	 */
	private String parseHtml(String html) {
		return Jsoup.parse(html).text();
	}
	
	/**
	 * Check if object not null & then get string
	 * 
	 * @param object Object to check
	 * @return Get string if not null, otherwise return empty string
	 */
	private String getStringIfNotNull(Object object) {
		return object == null ? "" : object.toString();
	}
	
	/**
	 * Reset all the variables of the class
	 */
	private void resetVariables() {
		// Reset variables
		termFreq.clear();
		tfSumDoc.clear();
		termFreqCollection.clear();
		totalDocs.reset();
		docFreq.clear();
		docsLength.clear();
		collectionLength.reset();
	}
	
}
