package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.Point;

@Service
public interface PointService {

	List<Point> getRoutePoints(Long routeId);
	
}
