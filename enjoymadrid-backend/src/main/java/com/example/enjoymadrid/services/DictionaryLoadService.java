package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface DictionaryLoadService {

	public void loadTerms(TouristicPoint point);
	
}
