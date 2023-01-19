package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.services.ModelService;

@Service
public class MixedMinAndMaxModelServiceLogic implements ModelService {
	
	//private final DictionaryRepository dictionaryRepository;
	
	public MixedMinAndMaxModelServiceLogic() {}

	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Extended Boolean Model (P-norm)
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param tfMax Max term frequency in a document/tourist point
	 * @param totalDocs Total number of documents/tourist points
	 * @param nTermDocs Number of documents where the term t appears
	 * @return 
	 */
	public double calculateScore(int tf, int tfMax, int totalDocs, int nTermDocs) {
		// (0.5 + (0.5*termFreq / maxTermFreq)) * log(totalNumberDocs)
		return (0.5 + 0.5*tf / tfMax) * Math.log10(totalDocs / nTermDocs);
	}

}
