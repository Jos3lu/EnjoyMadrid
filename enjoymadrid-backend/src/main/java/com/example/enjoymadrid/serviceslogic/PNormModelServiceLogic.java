package com.example.enjoymadrid.serviceslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.ModelService;

@Service
public class PNormModelServiceLogic implements ModelService {
	
	private DictionaryRepository dictionaryRepository;
	
	public PNormModelServiceLogic(DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
	}

	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void calculateScore(Map<String, Map<TouristicPoint, AtomicInteger>> termFreq, Map<TouristicPoint, AtomicInteger> maxTermFreq, 
			AtomicInteger totalDocs, Map<String, AtomicInteger> nTermDocs) {
		
		termFreq.entrySet().parallelStream().forEach(entryTerm -> {
			String term = entryTerm.getKey();
			Map<TouristicPoint, Double> weights = new HashMap<>();
			for (Entry<TouristicPoint, AtomicInteger> entryPoint: entryTerm.getValue().entrySet()) {
				// Get data to calculate score
				TouristicPoint touristicPoint = entryPoint.getKey();
				int tf = entryPoint.getValue().intValue();
				int maxTf = maxTermFreq.get(touristicPoint).intValue();
				int nDocs = nTermDocs.get(term).intValue();
				
				// Get tf-idf
				Double score = (0.5 + 0.5*tf / maxTf) * (totalDocs.intValue() / nDocs);
				
				// Save the score associated to the tourist point (document)
				weights.put(touristicPoint, score);
			}
			// Save the term & scores in DB
			this.dictionaryRepository.save(new Dictionary(term, weights));
		});

	}

}
