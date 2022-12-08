package com.example.enjoymadrid.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ForwardIndex {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Id of touristic point associated to the description/document
	private Long touristicPointId;
	
	// Max number of occurrences of a term in the document
	private Integer maxTermOccurrences;
	
	// Length of document in words/terms
	private Integer sizeDocument;
	
	public ForwardIndex() {}

	public ForwardIndex(Long touristicPointId, Integer maxTermOccurrences, Integer sizeDocument) {
		super();
		this.touristicPointId = touristicPointId;
		this.maxTermOccurrences = maxTermOccurrences;
		this.sizeDocument = sizeDocument;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTouristicPointId() {
		return touristicPointId;
	}

	public void setTouristicPointId(Long touristicPointId) {
		this.touristicPointId = touristicPointId;
	}

	public Integer getMaxTermOccurrences() {
		return maxTermOccurrences;
	}

	public void setMaxTermOccurrences(Integer maxTermOccurrences) {
		this.maxTermOccurrences = maxTermOccurrences;
	}

	public Integer getSizeDocument() {
		return sizeDocument;
	}

	public void setSizeDocument(Integer sizeDocument) {
		this.sizeDocument = sizeDocument;
	}
	
}
