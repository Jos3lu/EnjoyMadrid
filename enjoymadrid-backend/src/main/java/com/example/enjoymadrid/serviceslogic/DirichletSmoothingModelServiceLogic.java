package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DirichletSmoothingModelServiceLogic implements ModelService {

	//private final DictionaryRepository dictionaryRepository;
	
	public DirichletSmoothingModelServiceLogic() {}
	
	@Override
	public List<Dictionary> rank() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param tf Term frequencies in documents/tourist points
	 * @param tfCollection Term frequencies in collection
	 * @param docLength Length of the document D in words
	 * @param collectionLength Length of the collection C in words
	 * @return Score/weight of term T associated with document D
	 */
	@Override
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec) {
		// TODO Auto-generated method stub
		return 0;
	}

}
