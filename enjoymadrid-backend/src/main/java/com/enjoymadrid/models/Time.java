package com.enjoymadrid.models;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity
public class Time {

	@ElementCollection
	private List<LocalTime> times = new ArrayList<>();
	
	public Time() {}

	public List<LocalTime> getTimes() {
		return times;
	}

	public void setTimes(List<LocalTime> times) {
		this.times = times;
	}
	
}
