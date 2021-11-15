package com.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.TouristicPoint;
import com.enjoymadrid.model.repositories.TouristicPointRepository;
import com.enjoymadrid.services.TouristicPointService;

@Service
public class TouristicPointServiceLogic implements TouristicPointService {

	private final TouristicPointRepository touristicPointRepository;
	
	public TouristicPointServiceLogic(TouristicPointRepository touristicPointRepository) {
		this.touristicPointRepository = touristicPointRepository;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPointsByCategory(String category) {
		return this.touristicPointRepository.findByCategory(category).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Puntos no encontrados con la categoría: " + category));
	}

}
