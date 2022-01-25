package com.enjoymadrid.models;

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
	private Set<String> lines = new HashSet<>();
	
	@ManyToMany
	@MapKeyColumn(name = "LINE")
	@Column(name = "NEXT_POINT")
	private Map<String, PublicTransportPoint> nextStops = new HashMap<>();
	
	public PublicTransportPoint() {
		super();
	}
	
	public PublicTransportPoint(String name, Double longitude, Double latitude, String type) {
		super(name, longitude, latitude, type);
	}

	public Set<String> getLines() {
		return lines;
	}

	public void setLines(Set<String> lines) {
		this.lines = lines;
	}

	public Map<String, PublicTransportPoint> getNextStops() {
		return nextStops;
	}

	public void setNextStops(Map<String, PublicTransportPoint> nextStops) {
		this.nextStops = nextStops;
	}
	
}
