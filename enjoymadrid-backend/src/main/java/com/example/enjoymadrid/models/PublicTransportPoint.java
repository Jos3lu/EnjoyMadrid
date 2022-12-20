package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;

@Entity
@DiscriminatorValue("PublicTransport")
public class PublicTransportPoint extends TransportPoint {

	@ElementCollection
	private Set<String[]> stopLines = new HashSet<>();
	
	@ManyToMany
	@MapKeyColumn(name = "line")
	@Column(name = "next_point")
	private Map<String, PublicTransportPoint> nextStops = new HashMap<>();
	
	public PublicTransportPoint() {}
	
	public PublicTransportPoint(String name, Double longitude, Double latitude, String type, Set<String[]> stopLines) {
		super(name, longitude, latitude, type);
		this.stopLines = stopLines;
	}
	
	public PublicTransportPoint(PublicTransportPoint point) {
		super(point.getName(), point.getLongitude(), point.getLatitude(), point.getType());
		this.setId(point.getId());
		this.stopLines = new HashSet<>(point.getStopLines());
		this.nextStops = new HashMap<String, PublicTransportPoint>(point.getNextStops());
	}

	public Set<String[]> getStopLines() {
		return stopLines;
	}

	public void setStopLines(Set<String[]> stopLine) {
		this.stopLines = stopLine;
	}

	public Map<String, PublicTransportPoint> getNextStops() {
		return nextStops;
	}

	public void setNextStops(Map<String, PublicTransportPoint> nextStops) {
		this.nextStops = nextStops;
	}
	
}
