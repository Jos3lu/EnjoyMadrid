package com.example.enjoymadrid.models;

import java.util.HashMap;
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
@Table(name = "DICTIONARY_TABLE")
public class TermWeight {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Word/term to map to documents
	private String term;
		
	// Touristic place (documents) -> score/weight of term 
	@ElementCollection
	@MapKeyJoinColumn(name = "TOURISTIC_POINT_ID")
	@Column(name = "WEIGHT")
	@CollectionTable(name = "DICTIONARY_WEIGHTS_TABLE")
	private Map<TouristicPoint, Double> weights = new HashMap<>();
	
	// Number of occurrences of term T in the Collection / total number tokens in the Collection
	private Double probTermCol;
	
	public TermWeight() {}

	public TermWeight(String term, Map<TouristicPoint, Double> weights) {
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
