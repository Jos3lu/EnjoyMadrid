package com.example.enjoymadrid.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Collection {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Average length of documents (description of touristic points)
	private Integer avgLength;
	
	// Total number of documents
	private Integer nDocuments;
	
	// Total number of words in the collection
	private Integer nWords;
	
	public Collection() {}

	public Collection(Integer avgLength, Integer nDocuments, Integer nWords) {
		this.avgLength = avgLength;
		this.nDocuments = nDocuments;
		this.nWords = nWords;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAvgLength() {
		return avgLength;
	}

	public void setAvgLength(Integer avgLength) {
		this.avgLength = avgLength;
	}

	public Integer getnDocuments() {
		return nDocuments;
	}

	public void setnDocuments(Integer nDocuments) {
		this.nDocuments = nDocuments;
	}

	public Integer getnWords() {
		return nWords;
	}

	public void setnWords(Integer nWords) {
		this.nWords = nWords;
	}
	
}
