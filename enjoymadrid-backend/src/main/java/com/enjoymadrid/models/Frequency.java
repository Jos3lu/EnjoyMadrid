package com.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;

@Entity
public class Frequency extends Schedule {

	@ManyToMany
	@MapKeyColumn(name = "TIME_SLOT")
	@Column(name = "FREQUENCY")
	private Map<String, Integer> nextStops = new HashMap<>();
	
	public Frequency() {}

	public Map<String, Integer> getNextStops() {
		return nextStops;
	}

	public void setNextStops(Map<String, Integer> nextStops) {
		this.nextStops = nextStops;
	}
	
}
