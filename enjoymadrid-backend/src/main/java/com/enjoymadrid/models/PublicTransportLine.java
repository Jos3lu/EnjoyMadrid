package com.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

@Entity
public class PublicTransportLine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String line;
	
	private String direction;
	
	private String destination;
	
	private String color;
	
	@ElementCollection
	@MapKeyColumn(name = "STOP_ORDER")
	@Column(name = "POLYLINE_ID")
	private Map<String, Polyline> polylineStops = new HashMap<>();
	
	@ElementCollection
	@MapKeyColumn(name = "SCHEDULE_KEY")
	@Column(name = "SCHEDULE_VALUE")
	private Map<String, Schedule> scheduleStops = new HashMap<>();
	
	public PublicTransportLine() {}

	public PublicTransportLine(String line, String direction, String destination, String color) {
		this.line = line;
		this.direction = direction;
		this.destination = destination;
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Map<String, Polyline> getPolylineStops() {
		return polylineStops;
	}

	public void setPolylineStops(Map<String, Polyline> polylineStops) {
		this.polylineStops = polylineStops;
	}

	public Map<String, Schedule> getScheduleStops() {
		return scheduleStops;
	}

	public void setScheduleStops(Map<String, Schedule> scheduleStops) {
		this.scheduleStops = scheduleStops;
	}
	
}
