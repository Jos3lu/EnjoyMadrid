package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "POLYLINE_TABLE")
public class Polyline {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull(message = "Duración no puede estar vacío")
	private Double duration;
		
	@ElementCollection
	@CollectionTable(name = "POLYLINE_COORDINATES_TABLE")
	private List<Double[]> coordinates = new ArrayList<>();
	
	public Polyline() {}

	public Polyline(@NotNull(message = "Duración no puede estar vacío") Double duration, 
			List<Double[]> coordinates) {
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
