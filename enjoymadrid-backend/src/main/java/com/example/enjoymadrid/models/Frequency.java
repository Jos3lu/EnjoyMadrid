package com.example.enjoymadrid.models;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.MapKeyColumn;

@Entity
public class Frequency extends Schedule {
	
	@ElementCollection
	@MapKeyColumn(name = "TIME_SLOT")
	@Column(name = "FREQUENCY")
	private Map<String, Integer> dayFrequencies = new HashMap<>();
	
	private LocalTime startSchedule;
	
	private LocalTime endSchedule;
	
	public Frequency() {}
	
	public Frequency(Map<String, Integer> dayFrequencies, LocalTime startSchedule, LocalTime endSchedule) {
		super();
		this.dayFrequencies = dayFrequencies;
		this.startSchedule = startSchedule;
		this.endSchedule = endSchedule;
	}

	public Map<String, Integer> getDayFrequencies() {
		return dayFrequencies;
	}

	public void setDayFrequencies(Map<String, Integer> dayFrequencies) {
		this.dayFrequencies = dayFrequencies;
	}

	public LocalTime getStartSchedule() {
		return startSchedule;
	}

	public void setStartSchedule(LocalTime startSchedule) {
		this.startSchedule = startSchedule;
	}

	public LocalTime getEndSchedule() {
		return endSchedule;
	}

	public void setEndSchedule(LocalTime endSchedule) {
		this.endSchedule = endSchedule;
	}
	
}
