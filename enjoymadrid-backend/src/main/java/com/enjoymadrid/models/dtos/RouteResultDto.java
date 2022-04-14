package com.enjoymadrid.models.dtos;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.enjoymadrid.models.Segment;
import com.enjoymadrid.models.TransportPoint;
import com.enjoymadrid.models.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

public class RouteResultDto {
	
	@JsonView
	@NotNull(message = "Id cannot be empty")
	private Long id;

	@JsonView(RouteInterfaces.BasicRouteResponseData.class)
	@NotBlank(message = "Name cannot be empty")
	private String name;
					
	@JsonView(RouteInterfaces.BasicRouteResponseData.class)
	@NotNull(message = "Duration cannot be null")
	private Double duration;
	
	@JsonView(RouteInterfaces.PointsData.class)
	@NotEmpty(message = "Points cannot be empty")
	private List<TransportPoint> points = new ArrayList<>();
	
	@JsonView(RouteInterfaces.SegmentData.class)
	@NotEmpty(message = "Segments cannot be empty")
	private List<Segment> segments = new ArrayList<>();
	
	public RouteResultDto(@NotBlank(message = "Name cannot be empty") String name,
			@NotNull(message = "Duration cannot be null") Double duration,
			@NotEmpty(message = "Points cannot be empty") List<TransportPoint> points,
			@NotEmpty(message = "Segments cannot be empty") List<Segment> segments) {
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
