package com.example.enjoymadrid.models;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;

@Entity
public class Time extends Schedule {

	@ElementCollection
	@MapKeyColumn(name = "WEEK_DAY")
	@Lob
	@Column(name = "TIMES")
	private Map<String, LocalTime[]> dayTimes = new HashMap<>();
	
	public Time() {}
	
	public Time(Map<String, LocalTime[]> dayTimes) {
		super();
		this.dayTimes = dayTimes;
	}

	public Map<String, LocalTime[]> getDayTimes() {
		return dayTimes;
	}

	public void setDayTimes(Map<String, LocalTime[]> dayTimes) {
		this.dayTimes = dayTimes;
	}
	
}
