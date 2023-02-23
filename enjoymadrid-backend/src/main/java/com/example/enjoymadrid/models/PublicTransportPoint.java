package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("PublicTransport")
@Table(name = "PUBLIC_TRANSPORT_POINT_TABLE")
public class PublicTransportPoint extends TransportPoint {

	@ElementCollection
	@CollectionTable(name = "PUBLIC_TRANSPORT_POINT_LINES_TABLE")
	private Set<String[]> stopLines = new HashSet<>();
	
	@ManyToMany
	@MapKeyColumn(name = "LINE")
	@Column(name = "NEXT_POINT")
	@CollectionTable(name = "PUBLIC_TRANSPORT_POINT_NEXT_POINTS_TABLE")
	private Map<String, PublicTransportPoint> nextStops = new HashMap<>();
	
	public PublicTransportPoint() {}
	
	public PublicTransportPoint(String name, Double longitude, Double latitude, String type, 
			Map<String, Long> nearbyTouristicPoints, Set<String[]> stopLines) {
		super(name, longitude, latitude, type, nearbyTouristicPoints);
		this.stopLines = stopLines;
	}
	
	public PublicTransportPoint(PublicTransportPoint point) {
		super(point.getName(), point.getLongitude(), point.getLatitude(), point.getType(), point.getNearbyTouristicPoints());
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
