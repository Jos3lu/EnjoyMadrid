package com.enjoymadrid.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
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
import javax.validation.constraints.NotEmpty;
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
	@NotBlank(message = "Origin cannot be empty")
	private String origin;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotBlank(message = "Destination cannot be empty")
	private String destination;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Origin cannot be empty")
	private Integer maxDistance;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	@NotNull(message = "Mode of transports cannot be empty")
	private List<String> transports = new LinkedList<>();
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Date cannot be empty")
	private LocalDate date;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private Integer duration;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private Double distance;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@ElementCollection
	@MapKeyColumn(name = "PREFERENCES")
	@Column(name = "PREFRENCES_VALUE")
	@NotNull(message = "Preferences cannot be empty")
	private Map<String, Integer> preferences = new HashMap<>();
	
	//@ManyToMany
	//@JoinTable(name="ROUTE_POINT", joinColumns=@JoinColumn(name="ROUTE_ID"), inverseJoinColumns=@JoinColumn(name="POINT_ID"))
	//@JsonView(RouteInterfaces.PointsData.class)
	//private List<Point> points = new LinkedList<>();
		
	public Route() {}
	
	public Route(@NotBlank(message = "Name cannot be empty") String name,
			@NotBlank(message = "Origin cannot be empty") String origin,
			@NotBlank(message = "Destination cannot be empty") String destination,
			@NotNull(message = "Origin cannot be empty") Integer maxDistance,
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Integer maxDistance) {
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
