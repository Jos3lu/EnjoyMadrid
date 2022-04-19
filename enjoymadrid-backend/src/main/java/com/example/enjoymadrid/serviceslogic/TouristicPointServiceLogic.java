package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.services.TouristicPointService;

@Service
public class TouristicPointServiceLogic implements TouristicPointService {

	private final TouristicPointRepository touristicPointRepository;
	
	public TouristicPointServiceLogic(TouristicPointRepository touristicPointRepository) {
		this.touristicPointRepository = touristicPointRepository;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPointsByCategory(String category) {
		return this.touristicPointRepository.findByCategory(category);
	}

}
