package com.enjoymadrid.models;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.enjoymadrid.models.interfaces.TransportPointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TransportPoint extends Point {
		
	@JsonView(TransportPointInterfaces.BasicData.class)
	private String type;
					
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
	
}
