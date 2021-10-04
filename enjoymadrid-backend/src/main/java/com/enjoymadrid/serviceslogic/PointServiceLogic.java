package com.enjoymadrid.serviceslogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.repositories.PointRepository;
import com.enjoymadrid.services.PointService;

@Service
public class PointServiceLogic implements PointService {

	private PointRepository pointRepository;
	
	@Autowired
	public PointServiceLogic(PointRepository pointRepository) {
		this.pointRepository = pointRepository;
	}

	@Override
	public Point getPoint(Long id) {
		return pointRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point not found: " + id));
	}
	
}
