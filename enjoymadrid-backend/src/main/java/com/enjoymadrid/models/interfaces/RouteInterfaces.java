package com.enjoymadrid.models.interfaces;

public class RouteInterfaces {
	
	private RouteInterfaces() {}
	
	public static interface BasicData {}
		
	public static interface PointsData extends PointInterfaces.BasicData {}
	
	public static interface SegmentData extends SegmentInterfaces.BasicData {}
	
	public static interface CompleteData extends PointInterfaces.BasicData {}
			
	public static interface DetailData extends BasicData, PointsData {}
	
	public static interface GeneralData 
		extends BasicData, PointsData, SegmentData, CompleteData {}
	
}
