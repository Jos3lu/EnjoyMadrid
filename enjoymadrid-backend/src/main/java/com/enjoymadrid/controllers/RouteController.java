package com.enjoymadrid.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.TouristicPoint;
import com.enjoymadrid.model.Route;
import com.enjoymadrid.model.dtos.RouteDto;
import com.enjoymadrid.model.interfaces.UserInterfaces;
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
		List<Route> routes = this.routeService.getUserRoutes(userId);
		return routes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(routes);
	}
	
	@PostMapping("/routes")
	//@JsonView(RouteInterfaces.PointsData.class)
	public ResponseEntity<List<TouristicPoint>> createRoute(@Valid @RequestBody RouteDto routeDto) {
		LocalDate date = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate();
		return ResponseEntity.ok().build();
	}
	
}
