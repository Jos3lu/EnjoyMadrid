package com.enjoymadrid.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.enjoymadrid.models.TouristicPoint;
import com.enjoymadrid.services.TouristicPointService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class TouristicPointController {

	private final TouristicPointService touristicPointService;
	
	public TouristicPointController(TouristicPointService touristicPointService) {
		this.touristicPointService = touristicPointService;
	}
	
	@GetMapping("/tourist-points")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	public ResponseEntity<List<TouristicPoint>> getTouristicPointsByCategory(@RequestParam(defaultValue = "") String category) {
		return ResponseEntity.ok(this.touristicPointService.getTouristicPointsByCategory(category));
	}
	
}
