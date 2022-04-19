package com.example.enjoymadrid.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BicycleTransport")
public class BicycleTransportPoint extends TransportPoint {
	
	private String stationNumber;
	private Integer totalBases;
	private Integer dockBases;
	private Integer freeBases;
	private Boolean activate;
	private Boolean no_available;
	private Integer reservations;
	
	public BicycleTransportPoint() {
		super();
	}
	
	public BicycleTransportPoint(String stationNumber, String name, Double longitude, Double latitude, String type) {
		super(name, longitude, latitude, type);
		this.stationNumber = stationNumber;
	}
	
	public String getStationNumber() {
		return stationNumber;
	}
	
	public void setStationNumber(String stationNumber) {
		this.stationNumber = stationNumber;
	}

	public Integer getTotalBases() {
		return totalBases;
	}

	public void setTotalBases(Integer totalBases) {
		this.totalBases = totalBases;
	}

	public Integer getDockBases() {
		return dockBases;
	}

	public void setDockBases(Integer dockBases) {
		this.dockBases = dockBases;
	}

	public Integer getFreeBases() {
		return freeBases;
	}

	public void setFreeBases(Integer freeBases) {
		this.freeBases = freeBases;
	}

	public Boolean getActivate() {
		return activate;
	}

	public void setActivate(Boolean activate) {
		this.activate = activate;
	}

	public Boolean getNo_available() {
		return no_available;
	}

	public void setNo_available(Boolean no_available) {
		this.no_available = no_available;
	}

	public Integer getReservations() {
		return reservations;
	}

	public void setReservations(Integer reservations) {
		this.reservations = reservations;
	}
	
	public boolean isAvailable() {
		return activate && !no_available && (dockBases > 0) && ((freeBases - reservations) > 0);
	}
	
}
