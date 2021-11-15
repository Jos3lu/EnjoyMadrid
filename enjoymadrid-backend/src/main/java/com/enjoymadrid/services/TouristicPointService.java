package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.TouristicPoint;

@Service
public interface TouristicPointService {

	public List<TouristicPoint> getTouristicPointsByCategory(String category);
	
}
