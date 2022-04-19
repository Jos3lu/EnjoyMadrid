package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface TouristicPointService {

	public List<TouristicPoint> getTouristicPointsByCategory(String category);
	
}
