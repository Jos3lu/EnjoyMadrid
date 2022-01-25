package com.enjoymadrid.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.enjoymadrid.models.interfaces.SegmentInterfaces;
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
	@NotNull(message = "Distance cannot be null")
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
	private Map<Integer[], String> steps = new HashMap<>();
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@ElementCollection
	private List<Double[]> polyline = new ArrayList<>();
		
	public Segment() {}

	public Segment(@NotNull(message = "Source cannot be null") Integer source,
			@NotNull(message = "Target cannot be null") Integer target,
			@NotNull(message = "Distance cannot be null") Double distance,
			@NotNull(message = "Duration cannot be null") Double duration,
			@NotBlank(message = "Mode of transport cannot be empty") String transportMode, 
			List<Double[]> polyline) {
		this.source = source;
		this.target = target;
		this.distance = distance;
		this.duration = duration;
		this.transportMode = transportMode;
		this.polyline = polyline;
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

	public Map<Integer[], String> getSteps() {
		return steps;
	}

	public void setSteps(Map<Integer[], String> steps) {
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
	
}
