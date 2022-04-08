package com.enjoymadrid.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.enjoymadrid.models.Route;
import com.enjoymadrid.models.dtos.RouteResponseDto;
import com.enjoymadrid.services.RouteService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class RouteController {
	
	private final RouteService routeService;
	
	public RouteController(RouteService routeService) {
		this.routeService = routeService;
	}

	@GetMapping("/routes")
	@JsonView(RouteInterfaces.RouteData.class)
	public ResponseEntity<List<Route>> getUserRoutes(Principal principal) {
		return ResponseEntity.ok(this.routeService.getUserRoutes(principal != null ? principal.getName() : null));
	}
	
	@PostMapping("/routes")
	@JsonView(RouteInterfaces.RouteResponseData.class)
	public ResponseEntity<RouteResponseDto> createRoute(Principal principal, @Valid @RequestBody Route route) {
		LocalDate date = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate();
		route.setDate(date);
		return new ResponseEntity<>(this.routeService.createRoute(route, principal != null ? principal.getName() : null), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/routes/{routeId}")
	public ResponseEntity<Void> deleteRoute(Principal principal, @PathVariable Long routeId) {
		this.routeService.deleteRoute(routeId, principal != null ? principal.getName() : null);
		return ResponseEntity.ok().build();
	}
	
	
}
