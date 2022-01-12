package com.enjoymadrid.models;

public class PointWrapper<P extends Comparable<P>> implements Comparable<PointWrapper<P>>{

	private final P point;
	private PointWrapper<P> previous;
	private double distanceFromOrigin;
	private final double costHeuristic;
	private double totalCost;
	
	public PointWrapper(P point, PointWrapper<P> previous, double distanceFromOrigin, double costHeuristic) {
		this.point = point;
		this.previous = previous;
		this.distanceFromOrigin = distanceFromOrigin;
		this.costHeuristic = costHeuristic;
		calculateTotalCost();
	}
	
	private void calculateTotalCost() {
		this.totalCost = this.distanceFromOrigin + this.costHeuristic;
	}
	
	public PointWrapper<P> getPrevious() {
		return previous;
	}

	public void setPrevious(PointWrapper<P> previous) {
		this.previous = previous;
	}

	public double getDistanceFromOrigin() {
		return distanceFromOrigin;
	}

	public void setDistanceFromOrigin(double distanceFromOrigin) {
		this.distanceFromOrigin = distanceFromOrigin;
		calculateTotalCost();
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public P getPoint() {
		return point;
	}

	public double getCostHeuristic() {
		return costHeuristic;
	}

	@Override
	public int compareTo(PointWrapper<P> o) {
		int compare = Double.compare(this.totalCost, o.totalCost);
		if (compare == 0) {
			compare = point.compareTo(o.getPoint());
		}
		return compare;
	}
	
}
