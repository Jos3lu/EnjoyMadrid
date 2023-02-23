package com.example.enjoymadrid.models;

import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("Transport")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TRANSPORT_POINT_TABLE")
public class TransportPoint extends Point implements Comparable<TransportPoint> {
		
	private String type;
	
	@ElementCollection
	@MapKeyColumn(name = "PREFERENCE_TYPE")
	@Column(name = "NEARBY_TOURISTIC_POINTS_NUMBER")
	@CollectionTable(name = "NEARBY_TOURISTIC_POINTS_TABLE")
	private Map<String, Long> nearbyTouristicPoints;
					
	public TransportPoint() {}
	
	public TransportPoint(String name, Double longitude, Double latitude, String type, 
			Map<String, Long> nearbyTouristicPoints) {
		super(name, longitude, latitude);
		this.type = type;
		this.nearbyTouristicPoints = nearbyTouristicPoints;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Map<String, Long> getNearbyTouristicPoints() {
		return nearbyTouristicPoints;
	}
	
	public void setNearbyTouristicPoints(Map<String, Long> nearbyTouristicPoints) {
		this.nearbyTouristicPoints = nearbyTouristicPoints;
	}

	@Override
	public int compareTo(TransportPoint o) {
		return this.getName().compareTo(o.getName());
	}
	
}
