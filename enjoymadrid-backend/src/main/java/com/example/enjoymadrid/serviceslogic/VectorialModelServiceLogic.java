package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.services.ModelService;

@Service
public class VectorialModelServiceLogic implements ModelService {
	
	//private final DictionaryRepository dictionaryRepository;
	
	public VectorialModelServiceLogic() {}

	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Vectorial Model
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param nTermDocs Number of documents where the term t appears
	 */
	public double calculateScore(int tf, int totalDocs, int nTermDocs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
