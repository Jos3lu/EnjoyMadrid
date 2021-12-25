package com.enjoymadrid.models;

import javax.persistence.Entity;

@Entity
public class BycicleTransportPoint extends TransportPoint {

	private Integer totalBases;
	
	private Integer dockBases;
	
	private Integer freeBases;
	
	private Boolean activate;
	
	private Boolean no_available;
	
	public BycicleTransportPoint() {
		super();
	}
	
	public BycicleTransportPoint(String name, Double longitude, Double latitude, String type) {
		super(name, longitude, latitude, type);
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
	
}
