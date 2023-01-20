package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;

@Service
public interface ModelService {

	public List<Dictionary> rank();
	
	public double calculateScore(DictionaryScoreSpec dictionaryScoreSpec);
	
}
