package com.enjoymadrid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.interfaces.PointInterfaces;
import com.enjoymadrid.services.PointService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api/points")
public class PointController {
	
	private PointService pointService;
	
	@Autowired
	public PointController(PointService pointService) {
		this.pointService = pointService;
	}

	@GetMapping("/{id}")
	@JsonView(PointInterfaces.BasicData.class)
	public ResponseEntity<Point> getPoint(@PathVariable Long id) {
		return ResponseEntity.ok(pointService.getPoint(id));
	}
	
}
