package com.example.enjoymadrid.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@DiscriminatorValue("Transport")
@Inheritance(strategy = InheritanceType.JOINED)
public class TransportPoint extends Point implements Comparable<TransportPoint> {
		
	private String type;
					
	public TransportPoint() {}
	
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

	@Override
	public int compareTo(TransportPoint o) {
		return this.getName().compareTo(o.getName());
	}
	
}
