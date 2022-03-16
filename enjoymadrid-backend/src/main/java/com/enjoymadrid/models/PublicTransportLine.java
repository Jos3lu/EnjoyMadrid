package com.enjoymadrid.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	private String destination;
	
	private String direction;
	
	private String color;
	
	/*
	@ElementCollection
	@MapKeyColumn(name = "STATION_LINE")
	@Column(name = "INDEX_COORDINATES")
	private Map<String, Double[]> stationsPolyline = new HashMap<>();
	*/
	
	public PublicTransportLine() {
	}


	
}
