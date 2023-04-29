package com.example.enjoymadrid.services;

import com.example.enjoymadrid.models.TermWeightSpec;

public interface ModelService {

	/**
	 * Add/multiply the tourist point's term weight to the cumulative score 
	 * of the query terms
	 * 
	 * @param score Cumulative score of Tourist Point P for the given query
	 * @param weight Weight of term T associated to Tourist point P
	 * @param freq Frequency of current query term
	 * @return Weight of Term T associated to Tourist point P +/* cumulative score of Tourist point P for the given query
	 */
	public double rank(double score, double weight, int freq);
	
	/**
	 * Get term weight of a tourist point
	 * Models: Vector Space Model, Okapi BM25 Model, Dirichlet Smoothing Model
	 * 
	 * @param termWeightSpec Input data used for the IR Model
	 * @return Weight of term T associated to Tourist point P
	 */
	public double calculateWeight(TermWeightSpec termWeightSpec);
	
}
