package com.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.Route;
import com.enjoymadrid.model.repositories.RouteRepository;
import com.enjoymadrid.services.PointService;

@Service
public class PointServiceLogic implements PointService {

	private final RouteRepository routeRepository;
	
	public PointServiceLogic(RouteRepository routeRepository) {
		this.routeRepository = routeRepository;
	}

	@Override
	public List<Point> getRoutePoints(Long routeId) {
		Route route = this.routeRepository.findById(routeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no encontrada"));
		return route.getPoints();
	}

}
