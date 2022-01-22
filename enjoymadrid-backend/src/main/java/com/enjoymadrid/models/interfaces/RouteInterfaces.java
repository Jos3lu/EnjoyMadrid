package com.enjoymadrid.models.interfaces;

public class RouteInterfaces {
	
	private RouteInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface PointsData extends PointInterfaces.BasicData {}
	
	public static interface SegmentData extends SegmentInterfaces.GeneralData {}
	
	public static interface GeneralData extends BasicData, PointsData, SegmentData {}
	
}
