package com.enjoymadrid.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("PublicTransport")
public class PublicTransportPoint extends TransportPoint {

	@ElementCollection
	private Set<String> lines = new HashSet<>();
	
	@ManyToMany
	private Set<PublicTransportPoint> nextStops = new HashSet<>();
	
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
	
	public Set<PublicTransportPoint> getNextStops() {
		return nextStops;
	}

	public void setNextStops(Set<PublicTransportPoint> nextStops) {
		this.nextStops = nextStops;
	}
	
}
