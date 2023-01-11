package com.example.enjoymadrid.serviceslogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryService;

@Service
public class DictionaryServiceLogic implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	private DictionaryRepository dictionaryRepository;
	
	public DictionaryServiceLogic(DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
	}
	
	@Override
	public void deleteTouristicPointOfTerm(Dictionary term, TouristicPoint point) {
		term.getWeights().remove(point);
		this.dictionaryRepository.save(term);
	}

}
