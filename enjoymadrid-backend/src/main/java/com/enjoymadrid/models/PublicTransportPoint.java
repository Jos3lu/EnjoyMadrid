package com.enjoymadrid.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity
public class PublicTransportPoint extends TransportPoint {

	@ElementCollection
	private Set<String> lines = new HashSet<>();
	
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
	
}
