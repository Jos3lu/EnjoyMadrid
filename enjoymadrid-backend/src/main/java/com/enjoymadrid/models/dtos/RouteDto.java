package com.enjoymadrid.models.dtos;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.enjoymadrid.models.Point;

public class RouteDto {
		
	@NotBlank(message = "Name cannot be empty")
	private String name;
	
	@NotNull(message = "Origin cannot be empty")
	private Point origin;
	
	@NotNull(message = "Destination cannot be empty")
	private Point destination;
	
	@NotNull(message = "Origin cannot be empty")
	private Double maxDistance;
	
	@NotNull(message = "Mode of transports cannot be empty")
	private List<String> transports;
	
	@NotNull(message = "Preferences cannot be empty")
	private Map<String, Integer> preferences;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getOrigin() {
		return origin;
	}

	public void setOrigin(Point origin) {
		this.origin = origin;
	}

	public Point getDestination() {
		return destination;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}

	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public List<String> getTransports() {
		return transports;
	}

	public void setTransports(List<String> transports) {
		this.transports = transports;
	}

	public Map<String, Integer> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, Integer> preferences) {
		this.preferences = preferences;
	}
	
}
