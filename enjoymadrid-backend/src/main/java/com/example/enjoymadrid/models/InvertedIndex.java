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
	
	// Word to map to documents
	private String term;
	
	// Number of documents the term appears
	private Integer nDocumentsTerm;
	
	// Touristic place (documents) ID -> raw count of term
	@ElementCollection
	private Map<Integer, Integer> termOccurrences = new HashMap<>();
	
	// Number of occurrences of term in the collection
	private Integer nCollectionTerm;
	
	public InvertedIndex() {}

	public InvertedIndex(String term, Integer nDocumentsTerm, Map<Integer, Integer> termOccurrences,
			Integer nCollectionTerm) {
		this.term = term;
		this.nDocumentsTerm = nDocumentsTerm;
		this.termOccurrences = termOccurrences;
		this.nCollectionTerm = nCollectionTerm;
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

	public Integer getnDocumentsTerm() {
		return nDocumentsTerm;
	}

	public void setnDocumentsTerm(Integer nDocumentsTerm) {
		this.nDocumentsTerm = nDocumentsTerm;
	}

	public Map<Integer, Integer> getTermOccurrences() {
		return termOccurrences;
	}

	public void setTermOccurrences(Map<Integer, Integer> termOccurrences) {
		this.termOccurrences = termOccurrences;
	}
	
	public Integer getnCollectionTerm() {
		return nCollectionTerm;
	}

	public void setnCollectionTerm(Integer nCollectionTerm) {
		this.nCollectionTerm = nCollectionTerm;
	}

}
