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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Origin cannot be empty")
	@ManyToOne(cascade = CascadeType.ALL)
	private TransportPoint origin;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Destination cannot be empty")
	@ManyToOne(cascade = CascadeType.ALL)
	private TransportPoint destination;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Max distance cannot be empty")
	private Double maxDistance;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	@NotNull(message = "Mode of transports cannot be empty")
	private List<String> transports = new ArrayList<>();
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	@MapKeyColumn(name = "PREFERENCES")
	@Column(name = "PREFRENCES_VALUE")
	@NotNull(message = "Preferences cannot be empty")
	private Map<String, Integer> preferences = new HashMap<>();
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Date cannot be empty")
	private LocalDate date;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private Integer duration;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private Double distance;
		
	//@ManyToMany
	//@JoinTable(name="ROUTE_POINT", joinColumns=@JoinColumn(name="ROUTE_ID"), inverseJoinColumns=@JoinColumn(name="POINT_ID"))
	//@JsonView(RouteInterfaces.PointsData.class)
	//private List<Point> points = new ArrayList<>();
		
	public Route() {}
	
	public Route(@NotBlank(message = "Name cannot be empty") String name,
			@NotNull(message = "Origin cannot be empty") TransportPoint origin,
			@NotNull(message = "Destination cannot be empty") TransportPoint destination,
			@NotNull(message = "Max distance cannot be empty") Double maxDistance,
			@NotNull(message = "Mode of transports cannot be empty") List<String> transports,
			@NotNull(message = "Date cannot be empty") LocalDate date, 
			@NotNull(message = "Preferences cannot be empty") Map<String, Integer> preferences) {
		this.name = name;
		this.origin = origin;
		this.destination = destination;
		this.maxDistance = maxDistance;
		this.transports = transports;
		this.date = date;
		this.preferences = preferences;
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

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Map<String, Integer> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, Integer> preferences) {
		this.preferences = preferences;
	}
	
}
