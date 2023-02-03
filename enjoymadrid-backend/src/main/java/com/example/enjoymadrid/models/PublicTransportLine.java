package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;

@Entity
public class PublicTransportLine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String transportType;
	
	private String line;
	
	private String direction;
	
	private String destination;
	
	private String color;
	
	private Character scheduleType;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyColumn(name = "STOP_ORDER")
	@Column(name = "POLYLINE_ID")
	private Map<Integer, Polyline> stopPolylines = new HashMap<>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyColumn(name = "SCHEDULE_KEY")
	@Column(name = "SCHEDULE_VALUE")
	private Map<String, Schedule> stopSchedules = new HashMap<>();
		
	public PublicTransportLine() {}

	public PublicTransportLine(String transportType, String line, String direction, String destination, String color, 
			Character scheduleType, Map<Integer, Polyline> stopPolylines, Map<String, Schedule> stopSchedules) {
		this.transportType = transportType;
		this.line = line;
		this.direction = direction;
		this.destination = destination;
		this.color = color;
		this.scheduleType = scheduleType;
		this.stopPolylines = stopPolylines;
		this.stopSchedules = stopSchedules;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
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

	public Character getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(Character scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Map<Integer, Polyline> getStopPolylines() {
		return stopPolylines;
	}

	public void setStopPolylines(Map<Integer, Polyline> stopPolylines) {
		this.stopPolylines = stopPolylines;
	}

	public Map<String, Schedule> getStopSchedules() {
		return stopSchedules;
	}

	public void setStopSchedules(Map<String, Schedule> stopSchedules) {
		this.stopSchedules = stopSchedules;
	}
	
}
