package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.enjoymadrid.models.interfaces.SegmentInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

public class Segment {
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Punto origen no puede estar vacío")
	private Integer source;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Punto destino no puede estar vacío")
	private Integer target;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private Double distance;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Duración no puede estar vacío")
	private Double duration;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotBlank(message = "Modo de transporte no puede estar vacío")
	private String transportMode;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@ElementCollection
	@MapKeyColumn(name = "WAY_POINTS")
	@Column(name = "STEP")
	private List<String> steps = new ArrayList<>();
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@ElementCollection
	private List<Double[]> polyline = new ArrayList<>();
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private String line;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private String destination;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private String color;
		
	public Segment() {}

	public Segment(@NotNull(message = "Punto origen no puede estar vacío") Integer source,
			@NotNull(message = "Punto destino no puede estar vacío") Integer target,
			@NotBlank(message = "Modo de transporte no puede estar vacío") String transportMode) {
		this.source = source;
		this.target = target;
		this.transportMode = transportMode;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public List<String> getSteps() {
		return steps;
	}

	public void setSteps(List<String> steps) {
		this.steps = steps;
	}

	public List<Double[]> getPolyline() {
		return polyline;
	}

	public void setPolyline(List<Double[]> polyline) {
		this.polyline = polyline;
	}

	public String getTransportMode() {
		return transportMode;
	}

	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
}
