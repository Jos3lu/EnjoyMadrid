package com.example.enjoymadrid.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.example.enjoymadrid.models.interfaces.SegmentInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Segment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(SegmentInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Source cannot be null")
	private Integer source;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Target cannot be null")
	private Integer target;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private Double distance;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Duration cannot be null")
	private Double duration;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotBlank(message = "Mode of transport cannot be empty")
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

	public Segment(@NotNull(message = "Source cannot be null") Integer source,
			@NotNull(message = "Target cannot be null") Integer target,
			@NotBlank(message = "Mode of transport cannot be empty") String transportMode) {
		this.source = source;
		this.target = target;
		this.transportMode = transportMode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
