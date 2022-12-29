package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InvertedIndex {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Word/term to map to documents
	private String term;
			
	// Touristic place ID (documents) -> score/weight of term 
	@ElementCollection
	private Map<Integer, Double> weightDoc = new HashMap<>();
	
	public InvertedIndex() {}

	public InvertedIndex(String term, Map<Integer, Double> weightDoc) {
		this.term = term;
		this.weightDoc = weightDoc;
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

	public Map<Integer, Double> getWeightDoc() {
		return weightDoc;
	}

	public void setWeightDoc(Map<Integer, Double> weightDoc) {
		this.weightDoc = weightDoc;
	}

}
