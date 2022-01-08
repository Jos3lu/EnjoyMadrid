package com.enjoymadrid.models;

public class PointWrapper<N> implements Comparable<PointWrapper<N>>{

	private final N node;
	private PointWrapper<N> previous;
	private double totalCostFromStart;
	private final double minRemainingCostToTarget;
	private double costSum;
	
	public PointWrapper(N node, PointWrapper<N> previous, double totalCostFromStart, double minRemainingCostToTarget,
			double costSum) {
		this.node = node;
		this.previous = previous;
		this.totalCostFromStart = totalCostFromStart;
		this.minRemainingCostToTarget = minRemainingCostToTarget;
		this.costSum = costSum;
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
		// TODO Auto-generated method stub
		return 0;
	}
	
}
