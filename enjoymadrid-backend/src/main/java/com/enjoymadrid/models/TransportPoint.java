package com.enjoymadrid.models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.enjoymadrid.models.interfaces.TransportPointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TransportPoint extends Point {
		
	@JsonView(TransportPointInterfaces.BasicData.class)
	private String type;
				
	@ManyToMany
	@JoinTable(name = "transport_point_touristic_point", joinColumns = @JoinColumn(name = "transport_point_id"), inverseJoinColumns = @JoinColumn(name = "touristic_point_id"))
	private List<TouristicPoint> touristicPoints = new LinkedList<>();
	
	@ManyToMany
	@JoinTable(name = "transport_point_transport_points", joinColumns = @JoinColumn(name = "transport_point_id"), inverseJoinColumns = @JoinColumn(name = "tranport_points_id"))
	private List<TransportPoint> transportPoints = new LinkedList<>();
	
	public TransportPoint() {
		super();
	}

	public TransportPoint(String name, Double longitude, Double latitude, String type) {
		super(name, longitude, latitude);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
	
}
