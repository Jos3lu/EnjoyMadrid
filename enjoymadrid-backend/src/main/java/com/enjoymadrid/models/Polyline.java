package com.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Polyline {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull(message = "Duration cannot be empty")
	private Double duration;
		
	@ElementCollection
	private List<Double[]> coordinates = new ArrayList<>();
	
	public Polyline() {
	}

	public Polyline(Double duration, List<Double[]> coordinates) {
		this.duration = duration;
		this.coordinates = coordinates;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public List<Double[]> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Double[]> coordinates) {
		this.coordinates = coordinates;
	}
	
}
