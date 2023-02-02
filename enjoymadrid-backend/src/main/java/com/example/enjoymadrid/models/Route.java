package com.example.enjoymadrid.models;

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
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.example.enjoymadrid.models.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(RouteInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotBlank(message = "Nombre no puede estar vacío")
	private String name;
	
	@JsonView(RouteInterfaces.PointsData.class)
	@NotNull(message = "Punto origen no puede estar vacío")
	@OneToOne(cascade = CascadeType.ALL)
	private TransportPoint origin;
	
	@JsonView(RouteInterfaces.PointsData.class)
	@NotNull(message = "Punto destino no puede estar vacío")
	@OneToOne(cascade = CascadeType.ALL)
	private TransportPoint destination;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "Distancia máxima no puede estar vacío")
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
	private String date;
							
	public Route() {}
	
	public Route(@NotBlank(message = "Nombre no puede estar vacío") String name,
			@NotNull(message = "Punto origen no puede estar vacío") TransportPoint origin,
			@NotNull(message = "Punto destino no puede estar vacío") TransportPoint destination,
			@NotNull(message = "Distancia máxima no puede estar vacío") Double maxDistance, List<String> transports,
			Map<String, Integer> preferences, String date) {
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Map<String, Integer> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, Integer> preferences) {
		this.preferences = preferences;
	}
	
}
