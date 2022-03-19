package com.enjoymadrid.models;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;

@Entity
public class Time extends Schedule {

	@ElementCollection
	@MapKeyColumn(name = "WEEK_DAY")
	@Column(name = "TIMES")
	private Map<String, LocalTime[]> timesDay = new HashMap<>();
	
	public Time() {
		super();
	}
	
	public Time(Map<String, LocalTime[]> timesDay) {
		super();
		this.timesDay = timesDay;
	}

	public Map<String, LocalTime[]> getTimesDay() {
		return timesDay;
	}

	public void setTimesDay(Map<String, LocalTime[]> timesDay) {
		this.timesDay = timesDay;
	}
	
}
