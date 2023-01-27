package com.example.enjoymadrid.models;

public class DictionaryScoreSpec {
	
	private int tf;
	private int totalDocs;
	private int docFreq;
	private double tfSumDoc;
	private double avgDoc;
	private int docLength;
	private double probTermCol;
		
	// Vector Space Model Constructor
	public DictionaryScoreSpec(int tf, int totalDocs, int docFreq, double tfSumDoc) {
		this.tf = tf;
		this.totalDocs = totalDocs;
		this.docFreq = docFreq;
		this.tfSumDoc = tfSumDoc;
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
	public DictionaryScoreSpec(int tf, int docLength, double probTermCol) {
		this.tf = tf;
		this.docLength = docLength;
		this.probTermCol = probTermCol;
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
	
	public double getTfSumDoc() {
		return tfSumDoc;
	}

	public void setTfSumDoc(double tfSumDoc) {
		this.tfSumDoc = tfSumDoc;
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

	public double getProbTermCol() {
		return probTermCol;
	}

	public void setProbTermCol(double probTermCol) {
		this.probTermCol = probTermCol;
	}

}
