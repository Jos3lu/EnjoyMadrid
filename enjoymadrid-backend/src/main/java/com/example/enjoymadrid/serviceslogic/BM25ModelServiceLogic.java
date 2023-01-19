package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.services.ModelService;

@Service
public class BM25ModelServiceLogic implements ModelService {

	//private final DictionaryRepository dictionaryRepository;
	
	public BM25ModelServiceLogic() {}
	
	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param nTermDocs Number of documents where the term t appears
	 * @param docLength Length of the document D in words
	 * @return
	 */
	public double calculateScore(int tf, int totalDocs, int nTermDocs, int docLength) {
		// TODO Auto-generated method stub
		return 0;
	}

}
