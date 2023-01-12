package com.example.enjoymadrid.services;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface DictionaryService {

	public List<String> analyze(String text, Analyzer analyzer);
	
	public String stem(String term); 
	
	public void deleteTouristicPointOfTerm(Dictionary term, TouristicPoint point);
	
}
