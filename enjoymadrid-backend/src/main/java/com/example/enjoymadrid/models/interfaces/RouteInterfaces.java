package com.example.enjoymadrid.models.interfaces;

public class RouteInterfaces {
	
	private RouteInterfaces() {}
	
	public static interface BasicData {}
		
	public static interface PointsData extends PointInterfaces.BasicData {}
			
	public static interface RouteData extends BasicData, PointsData {}
	
	public static interface BasicRouteResponseData {}
		
	public static interface SegmentData extends SegmentInterfaces.BasicData {}
			
	public static interface RouteResponseData extends BasicRouteResponseData, PointsData, SegmentData {}
	
}
