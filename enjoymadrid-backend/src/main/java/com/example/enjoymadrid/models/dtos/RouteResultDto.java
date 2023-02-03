package com.example.enjoymadrid.models.dtos;

import java.util.ArrayList;
import java.util.List;

import com.example.enjoymadrid.models.Segment;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

public class RouteResultDto {
	
	@JsonView(RouteInterfaces.BasicRouteResponseData.class)
	private Long id;

	@JsonView(RouteInterfaces.BasicRouteResponseData.class)
	private String name;
					
	@JsonView(RouteInterfaces.BasicRouteResponseData.class)
	private Double duration;
	
	@JsonView(RouteInterfaces.PointsData.class)
	private List<TransportPoint> points = new ArrayList<>();
	
	@JsonView(RouteInterfaces.SegmentData.class)
	private List<Segment> segments = new ArrayList<>();
	
	public RouteResultDto(String name, Double duration, List<TransportPoint> points, 
			List<Segment> segments) {
		this.name = name;
		this.duration = duration;
		this.points = points;
		this.segments = segments;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public List<TransportPoint> getPoints() {
		return points;
	}

	public void setPoints(List<TransportPoint> points) {
		this.points = points;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}
	
}
