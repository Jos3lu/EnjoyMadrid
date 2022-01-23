package com.enjoymadrid.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.enjoymadrid.models.interfaces.SegmentInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Segment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(SegmentInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(SegmentInterfaces.PointsData.class)
	@NotNull(message = "Source cannot be null")
	@ManyToOne
	private TransportPoint source;
	
	@JsonView(SegmentInterfaces.PointsData.class)
	@NotNull(message = "Target cannot be null")
	@ManyToOne
	private TransportPoint target;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Distance cannot be null")
	private Double distance;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@NotNull(message = "Duration cannot be null")
	private Double duration;
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@ElementCollection
	private Map<Integer[], String> steps = new HashMap<>();
	
	@JsonView(SegmentInterfaces.BasicData.class)
	@ElementCollection
	private List<Double[]> polyline = new ArrayList<>();
	
	@JsonView(SegmentInterfaces.BasicData.class)
	private String line;
	
	public Segment() {}

	public Segment(@NotNull(message = "Source cannot be null") TransportPoint source,
			@NotNull(message = "Target cannot be null") TransportPoint target,
			@NotNull(message = "Distance cannot be null") Double distance,
			@NotNull(message = "Duration cannot be null") Double duration, Map<Integer[], String> steps,
			List<Double[]> polyline) {
		this.source = source;
		this.target = target;
		this.distance = distance;
		this.duration = duration;
		this.steps = steps;
		this.polyline = polyline;
	}
	
}
