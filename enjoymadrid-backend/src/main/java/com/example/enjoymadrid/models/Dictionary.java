package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyJoinColumn;

@Entity
public class Dictionary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Word/term to map to documents
	private String term;
		
	// Touristic place (documents) -> score/weight of term 
	@ElementCollection
	@MapKeyJoinColumn(name = "touristic_point_id")
	@Column(name = "weight")
	private Map<TouristicPoint, Double> weights = new HashMap<>();
	
	// Number of occurrences of term T in the Collection / total number tokens in the Collection
	private Double probTermCol;
	
	public Dictionary() {}

	public Dictionary(String term, Map<TouristicPoint, Double> weights) {
		this.term = term;
		this.weights = weights;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Map<TouristicPoint, Double> getWeights() {
		return weights;
	}

	public void setWeights(Map<TouristicPoint, Double> weights) {
		this.weights = weights;
	}

	public Double getProbTermCol() {
		return probTermCol;
	}

	public void setProbTermCol(Double probTermCol) {
		this.probTermCol = probTermCol;
	}

}
