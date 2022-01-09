package com.enjoymadrid.models;

public class PointWrapper<N> implements Comparable<PointWrapper<N>>{

	private final N node;
	private PointWrapper<N> previous;
	private double totalCostFromStart;
	private final double minRemainingCostToTarget;
	private double costSum;
	
	public PointWrapper(N node, PointWrapper<N> previous, double totalCostFromStart, double minRemainingCostToTarget) {
		this.node = node;
		this.previous = previous;
		this.totalCostFromStart = totalCostFromStart;
		this.minRemainingCostToTarget = minRemainingCostToTarget;
		calculateCostSum();
	}
	
	private void calculateCostSum() {
		this.costSum = this.totalCostFromStart + this.minRemainingCostToTarget;
	}
	
	public PointWrapper<N> getPrevious() {
		return previous;
	}

	public void setPrevious(PointWrapper<N> previous) {
		this.previous = previous;
	}

	public double getTotalCostFromStart() {
		return totalCostFromStart;
	}

	public void setTotalCostFromStart(double totalCostFromStart) {
		this.totalCostFromStart = totalCostFromStart;
		calculateCostSum();
	}

	public double getCostSum() {
		return costSum;
	}

	public void setCostSum(double costSum) {
		this.costSum = costSum;
	}

	public N getNode() {
		return node;
	}

	public double getMinRemainingCostToTarget() {
		return minRemainingCostToTarget;
	}

	@Override
	public int compareTo(PointWrapper<N> o) {
		return 0;
	}
	
}
