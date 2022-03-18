package com.enjoymadrid.models;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity
public class Time extends Schedule {

	@ElementCollection
	private Map<String, LocalTime[]> timesDay = new HashMap<>();
	
	public Time() {}

	public Map<String, LocalTime[]> getTimesDay() {
		return timesDay;
	}

	public void setTimesDay(Map<String, LocalTime[]> timesDay) {
		this.timesDay = timesDay;
	}
	
}
