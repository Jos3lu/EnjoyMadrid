package com.example.enjoymadrid.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;

@Entity
public class InvertedIndex {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Word/term to map to documents
	private String term;
			
	// Touristic place (documents) -> score/weight of term 
	@ElementCollection
	@JoinTable(name = "terms_touristic_point",
			joinColumns = @JoinColumn(name = "term_id"))
	@MapKeyJoinColumn(name = "touristic_point_id")
	@Column(name = "weight")
	private Map<TouristicPoint, Double> weightDoc = new HashMap<>();
	
	public InvertedIndex() {}

	public InvertedIndex(String term, Map<TouristicPoint, Double> weightDoc) {
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

	public Map<TouristicPoint, Double> getWeightDoc() {
		return weightDoc;
	}

	public void setWeightDoc(Map<TouristicPoint, Double> weightDoc) {
		this.weightDoc = weightDoc;
	}

}
