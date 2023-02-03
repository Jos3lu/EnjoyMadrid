package com.example.enjoymadrid.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("BicycleTransport")
@Table(name = "BICYCLE_TRANSPORT_POINT_TABLE")
public class BicycleTransportPoint extends TransportPoint {
	
	private String stationNumber;
	private Integer totalBases;
	private Integer dockBases;
	private Integer freeBases;
	private Boolean activate;
	private Boolean no_available;
	private Integer reservations;
	
	public BicycleTransportPoint() {}
	
	public BicycleTransportPoint(String stationNumber, String name, Double longitude, Double latitude, String type, 
			Integer totalBases, Integer dockBases, Integer freeBases, Boolean activate, Boolean no_available, Integer reservations) {
		super(name, longitude, latitude, type);
		this.stationNumber = stationNumber;
		this.totalBases = totalBases;
		this.dockBases = dockBases;
		this.freeBases = freeBases;
		this.activate = activate;
		this.no_available = no_available;
		this.reservations = reservations;
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
