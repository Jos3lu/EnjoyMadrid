package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface TermLoadService {

	public void loadTerms(TouristicPoint touristicPoint);
	
	public void deleteTerms(TouristicPoint touristicPoint);
	
}
