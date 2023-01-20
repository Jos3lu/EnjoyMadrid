package com.example.enjoymadrid.models;

public class DictionaryScoreSpec {
	
	private int tf;
	private int totalDocs;
	private int docFreq;
	private double avgDoc;
	private int docLength;
	private int tfCollection;
	private long collectionLength;
	
	// MMM & Vector Space Model Constructor
	public DictionaryScoreSpec(int tf, int totalDocs, int docFreq) {
		this.tf = tf;
		this.totalDocs = totalDocs;
		this.docFreq = docFreq;
	}
	
	// BM25 Model Constructor
	public DictionaryScoreSpec(int tf, int totalDocs, int docFreq, int docLength, double avgDoc) {
		this.tf = tf;
		this.totalDocs = totalDocs;
		this.docFreq = docFreq;
		this.docLength = docLength;
		this.avgDoc = avgDoc;
	}
	
	// Dirichlet Model Constructor
	public DictionaryScoreSpec(int tf, int tfCollection, int docLength, long collectionLength) {
		this.tf = tf;
		this.tfCollection = tfCollection;
		this.docLength = docLength;
		this.collectionLength = collectionLength;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public int getTotalDocs() {
		return totalDocs;
	}

	public void setTotalDocs(int totalDocs) {
		this.totalDocs = totalDocs;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}

	public double getAvgDoc() {
		return avgDoc;
	}

	public void setAvgDoc(double avgDoc) {
		this.avgDoc = avgDoc;
	}

	public int getDocLength() {
		return docLength;
	}

	public void setDocLength(int docLength) {
		this.docLength = docLength;
	}

	public int getTfCollection() {
		return tfCollection;
	}

	public void setTfCollection(int tfCollection) {
		this.tfCollection = tfCollection;
	}

	public long getCollectionLength() {
		return collectionLength;
	}

	public void setCollectionLength(long collectionLength) {
		this.collectionLength = collectionLength;
	}

}
