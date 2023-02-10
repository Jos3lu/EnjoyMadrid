package com.example.enjoymadrid.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.example.enjoymadrid.services.TouristicPointService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class TouristicPointController {

	private final TouristicPointService touristicPointService;
	
	public TouristicPointController(TouristicPointService touristicPointService) {
		this.touristicPointService = touristicPointService;
	}
	
	@GetMapping("/tourist-points/search-category")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	public ResponseEntity<List<TouristicPoint>> getTouristicPointsByCategory(@RequestParam(defaultValue = "") String category) {
		return ResponseEntity.ok(this.touristicPointService.getTouristicPointsByCategory(category));
	}
	
	@GetMapping("/tourist-points/search-query")
	@JsonView(TouristicPointInterfaces.BasicData.class)
	public ResponseEntity<List<TouristicPoint>> getTouristicPointsByQuery(@RequestParam(defaultValue = "") String query) {
		return ResponseEntity.ok(this.touristicPointService.getTouristicPointsByQuery(query));
	}
	
	@GetMapping("/users/{userId}/tourist-points")
	@JsonView(UserInterfaces.TouristicPointData.class)
	public ResponseEntity<List<TouristicPoint>> getUserTouristicPoints(@PathVariable Long userId) {
		return ResponseEntity.ok(this.touristicPointService.getUserTouristicPoints(userId));
	}
		
	@PostMapping("/users/{userId}/tourist-points/{touristPointId}")
	public ResponseEntity<Void> addTouristicPointToUser(@PathVariable Long userId, @PathVariable Long touristPointId) {
		this.touristicPointService.addTouristicPointToUser(userId, touristPointId);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@DeleteMapping("/users/{userId}/tourist-points/{touristPointId}")
	public ResponseEntity<Void> deleteUserTouristicPoint(@PathVariable Long userId, @PathVariable Long touristPointId) {
		this.touristicPointService.deleteUserTouristicPoint(userId, touristPointId);
		return ResponseEntity.noContent().build();
	}
	
}
