package com.example.enjoymadrid.models;

public class PointWrapper<P extends Comparable<P>> implements Comparable<PointWrapper<P>>{

	private P point;
	private PointWrapper<P> previous;
	private double distanceFromOrigin;
	private final double costHeuristic;
	private boolean directNeighbor;
	private double totalCost;
	
	public PointWrapper(P point, PointWrapper<P> previous, boolean directNeighbor, double distanceFromOrigin, double costHeuristic) {
		this.point = point;
		this.previous = previous;
		this.distanceFromOrigin = distanceFromOrigin;
		this.costHeuristic = costHeuristic;
		this.directNeighbor = directNeighbor;
		calculateTotalCost();
	}
	
	private void calculateTotalCost() {
		this.totalCost = (this.distanceFromOrigin + this.costHeuristic) * (this.directNeighbor ? 0.25 : 1);
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

	public P getPoint() {
		return point;
	}
	
	public void setPoint(P point) {
		this.point = point;
	}

	public double getCostHeuristic() {
		return costHeuristic;
	}
	
	public boolean directNeighbor() {
		return directNeighbor;
	}

	public void setDirectNeighbor(boolean directNeighbor) {
		this.directNeighbor = directNeighbor;
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
