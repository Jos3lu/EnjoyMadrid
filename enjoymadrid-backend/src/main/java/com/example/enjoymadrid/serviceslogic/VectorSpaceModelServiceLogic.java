package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
public class VectorSpaceModelServiceLogic implements ModelService {
	
	//private final DictionaryRepository dictionaryRepository;
	
	public VectorSpaceModelServiceLogic() {}

	@Override
	public List<Dictionary> rank() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculate the score of a document/tourist point using the Vectorial Model
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param totalDocs Total number of documents/tourist points
	 * @param docFreq Number of documents where the term T appears
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec) {
		double query_weight = Math.log(dictionaryScoreSpec.getTotalDocs()/dictionaryScoreSpec.getDocFreq());
		double doc_weight = 0;
		return 0;
	}

}
