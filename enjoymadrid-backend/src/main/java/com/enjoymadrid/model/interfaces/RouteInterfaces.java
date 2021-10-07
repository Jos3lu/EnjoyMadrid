package com.enjoymadrid.model.interfaces;

public class RouteInterfaces {
	
	private RouteInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface PointsData extends PointInterfaces.BasicData {}
	
	public static interface UserData extends UserInterfaces.BasicData {}
	
}
