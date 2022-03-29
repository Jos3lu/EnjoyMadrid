package com.enjoymadrid.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.enjoymadrid.models.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(RouteInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotBlank(message = "Name cannot be empty")
	private String name;
	
	@JsonView(RouteInterfaces.PointsData.class)
	@NotNull(message = "Origin cannot be null")
	@OneToOne(cascade = CascadeType.ALL)
	private TransportPoint origin;
	
	@JsonView(RouteInterfaces.PointsData.class)
	@NotNull(message = "Destination cannot be null")
	@OneToOne(cascade = CascadeType.ALL)
	private TransportPoint destination;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Max distance cannot be null")
	private Double maxDistance;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	private List<String> transports = new ArrayList<>();
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	@MapKeyColumn(name = "PREFERENCE")
	@Column(name = "PREFRENCE_INTEREST")
	private Map<String, Integer> preferences = new HashMap<>();
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Date cannot be null")
	private LocalDate date;
	
	@JsonView(RouteInterfaces.CompleteData.class)
	@NotNull(message = "Distance cannot be null")
	private Double distance;
	
	@JsonView(RouteInterfaces.CompleteData.class)
	@NotNull(message = "Duration cannot be null")
	private Double duration;
	
	@ManyToMany
	@JsonView(RouteInterfaces.CompleteData.class)
	@Fetch(FetchMode.SELECT)
	private List<TransportPoint> points = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView(RouteInterfaces.SegmentData.class)
	private List<Segment> segments = new ArrayList<>();
					
	public Route() {}
	
	public Route(@NotBlank(message = "Name cannot be empty") String name,
			@NotNull(message = "Origin cannot be null") TransportPoint origin,
			@NotNull(message = "Destination cannot be null") TransportPoint destination,
			@NotNull(message = "Max distance cannot be null") Double maxDistance, List<String> transports,
			Map<String, Integer> preferences, @NotNull(message = "Date cannot be null") LocalDate date) {
		this.name = name;
		this.origin = origin;
		this.destination = destination;
		this.maxDistance = maxDistance;
		this.transports = transports;
		this.preferences = preferences;
		this.date = date;
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

	public TransportPoint getOrigin() {
		return origin;
	}

	public void setOrigin(TransportPoint origin) {
		this.origin = origin;
	}

	public TransportPoint getDestination() {
		return destination;
	}

	public void setDestination(TransportPoint destination) {
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Map<String, Integer> getPreferences() {
		return preferences;
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

	public void setPreferences(Map<String, Integer> preferences) {
		this.preferences = preferences;
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
