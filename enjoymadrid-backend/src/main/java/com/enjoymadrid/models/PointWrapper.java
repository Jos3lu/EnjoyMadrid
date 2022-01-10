package com.enjoymadrid.models;

public class PointWrapper<N extends Comparable<N>> implements Comparable<PointWrapper<N>>{

	private final N point;
	private PointWrapper<N> previous;
	private double totalCostFromStart;
	private final double minRemainingCostToTarget;
	private double costSum;
	
	public PointWrapper(N point, PointWrapper<N> previous, double totalCostFromStart, double minRemainingCostToTarget) {
		this.point = point;
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

	public N getPoint() {
		return point;
	}

	public double getMinRemainingCostToTarget() {
		return minRemainingCostToTarget;
	}

	@Override
	public int compareTo(PointWrapper<N> o) {
		int compare = Double.compare(this.costSum, o.costSum);
		if (compare == 0) {
			compare = point.compareTo(o.getPoint());
		}
		return compare;
	}
	
}
