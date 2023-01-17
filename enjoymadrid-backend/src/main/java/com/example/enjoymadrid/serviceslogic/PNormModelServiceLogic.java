package com.example.enjoymadrid.serviceslogic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.services.ModelService;

@Service
public class PNormModelServiceLogic implements ModelService {

	@Override
	public List<Dictionary> rankDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void calculateScore(Map<String, Map<TouristicPoint, AtomicInteger>> termFreq, Map<TouristicPoint, AtomicInteger> maxTermFreq, 
			AtomicInteger totalDocs, Map<String, AtomicInteger> nTermDocs) {
		
	}

}
