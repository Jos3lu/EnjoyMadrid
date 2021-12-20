package com.enjoymadrid.models;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.enjoymadrid.models.interfaces.TransportPointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class TransportPoint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(TransportPointInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(TransportPointInterfaces.BasicData.class)
	private String name;
	
	@JsonView(TransportPointInterfaces.BasicData.class)
	private Double longitude;
	
	@JsonView(TransportPointInterfaces.BasicData.class)
	private Double latitude;
	
	@JsonView(TransportPointInterfaces.BasicData.class)
	private String type;
		
	@ElementCollection
	private Set<String> lines = new HashSet<>();
		
	@ManyToMany
	@JoinTable(name = "transport_point_touristic_point", joinColumns = @JoinColumn(name = "transport_point_id"), inverseJoinColumns = @JoinColumn(name = "touristic_point_id"))
	private List<TouristicPoint> touristicPoints = new LinkedList<>();
	
	@ManyToMany
	@JoinTable(name = "transport_point_transport_points", joinColumns = @JoinColumn(name = "transport_point_id"), inverseJoinColumns = @JoinColumn(name = "tranport_points_id"))
	private List<TransportPoint> transportPoints = new LinkedList<>();
	
	public TransportPoint() {}

	public TransportPoint(String name, String type, Double longitude, Double latitude) {
		this.name = name;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Set<String> getLines() {
		return lines;
	}

	public void setLines(Set<String> lines) {
		this.lines = lines;
	}

	public List<TouristicPoint> getTouristicPoints() {
		return touristicPoints;
	}

	public void setTouristicPoints(List<TouristicPoint> touristicPoints) {
		this.touristicPoints = touristicPoints;
	}

	public List<TransportPoint> getTransportPoints() {
		return transportPoints;
	}

	public void setTransportPoints(List<TransportPoint> transportPoints) {
		this.transportPoints = transportPoints;
	}

	@Override
	public String toString() {
		return "TransportPoint [id=" + id + ", name=" + name + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", type=" + type + ", lines=" + lines + ", touristicPoints=" + touristicPoints + ", transportPoints="
				+ transportPoints + "]";
	}
	
}
