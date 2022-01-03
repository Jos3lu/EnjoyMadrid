package com.enjoymadrid.models.dtos;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RouteDto {
		
	@NotBlank(message = "Name cannot be empty")
	private String name;
	
	@NotBlank(message = "Origin cannot be empty")
	private String origin;
	
	@NotBlank(message = "Destination cannot be empty")
	private String destination;
	
	@NotNull(message = "Origin cannot be empty")
	private Integer maxDistance;
	
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Integer maxDistance) {
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
