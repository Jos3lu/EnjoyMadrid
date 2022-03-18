package com.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;

@Entity
public class Frequency extends Schedule {

	@ElementCollection
	@MapKeyColumn(name = "TIME_SLOT")
	@Column(name = "FREQUENCY")
	private Map<String, Integer> frequenciesDay = new HashMap<>();
	
	public Frequency() {}

	public Map<String, Integer> getFrequenciesDay() {
		return frequenciesDay;
	}

	public void setFrequenciesDay(Map<String, Integer> frequenciesDay) {
		this.frequenciesDay = frequenciesDay;
	}
	
}
