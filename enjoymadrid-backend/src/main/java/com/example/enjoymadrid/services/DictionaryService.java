package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface DictionaryService {

	public void deleteTouristicPointOfTerm(Dictionary term, TouristicPoint point);
	
}
