package com.enjoymadrid.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.models.interfaces.RouteInterfaces;
import com.enjoymadrid.models.interfaces.UserInterfaces;
import com.enjoymadrid.models.Route;
import com.enjoymadrid.models.dtos.RouteResultDto;
import com.enjoymadrid.services.RouteService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class RouteController {
	
	private final RouteService routeService;
	
	public RouteController(RouteService routeService) {
		this.routeService = routeService;
	}

	@GetMapping("/users/{userId}/routes")
	@JsonView(UserInterfaces.RouteData.class)
	public ResponseEntity<List<Route>> getUserRoutes(@PathVariable Long userId) {
		return ResponseEntity.ok(this.routeService.getUserRoutes(userId));
	}
	
	@PostMapping("/routes")
	@JsonView(RouteInterfaces.RouteResponseData.class)
	public ResponseEntity<RouteResultDto> createRoute(@Valid @RequestBody Route route) {
		// Create route for user not logged in
		return new ResponseEntity<>(this.routeService.createRoute(route, null), HttpStatus.CREATED);
	}
	
	@PostMapping("/users/{userId}/routes")
	@JsonView(RouteInterfaces.RouteResponseData.class)
	public ResponseEntity<RouteResultDto> createRoute(@Valid @RequestBody Route route, @PathVariable Long userId) {
		// Create route for user logged in
		return new ResponseEntity<RouteResultDto>(this.routeService.createRoute(route, userId), HttpStatus.CREATED);
	}
	
	@DeleteMapping("users/{userId}/routes/{routeId}")
	public ResponseEntity<Void> deleteRoute(@PathVariable Long userId, @PathVariable Long routeId) {
		this.routeService.deleteRoute(routeId, userId);
		return ResponseEntity.ok().build();
	}
	
	
}
