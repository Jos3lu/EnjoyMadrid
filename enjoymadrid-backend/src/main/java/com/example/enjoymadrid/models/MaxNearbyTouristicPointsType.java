package com.example.enjoymadrid.models;

import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "MAX_NEARBY_TOURISTIC_POINTS_BY_TYPE_TABLE")
public class MaxNearbyTouristicPointsType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ElementCollection
	@MapKeyJoinColumn(name = "PREFRERENCE_TYPE")
	@Column(name = "MAX_NEARBY_TOURISTIC_POINTS")
	@CollectionTable(name = "MAX_NEARBY_TOURISTIC_POINTS_TABLE")
	private Map<String, Long> maxNearbyTouristicPoints;
	
	public MaxNearbyTouristicPointsType() {}
	
	public MaxNearbyTouristicPointsType(Map<String, Long> maxNearbyTouristicPoints) {
		this.maxNearbyTouristicPoints = maxNearbyTouristicPoints;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Map<String, Long> getMaxNearbyTouristicPoints() {
		return maxNearbyTouristicPoints;
	}
	
	public void setMaxNearbyTouristicPoints(Map<String, Long> maxNearbyTouristicPoints) {
		this.maxNearbyTouristicPoints = maxNearbyTouristicPoints;
	}
	
}
