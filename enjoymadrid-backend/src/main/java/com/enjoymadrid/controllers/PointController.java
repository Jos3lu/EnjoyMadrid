package com.enjoymadrid.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.interfaces.RouteInterfaces;
import com.enjoymadrid.services.PointService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class PointController {
	
	private PointService pointService;
	
	@Autowired
	public PointController(PointService pointService) {
		this.pointService = pointService;
	}
	
	@GetMapping("/routes/{routeId}/points")
	@JsonView(RouteInterfaces.PointsData.class)
	public ResponseEntity<List<Point>> getRoutePoints(@PathVariable Long routeId) {
		List<Point> points = this.pointService.getRoutePoints(routeId);
		return points.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(points);
	}
	
}
