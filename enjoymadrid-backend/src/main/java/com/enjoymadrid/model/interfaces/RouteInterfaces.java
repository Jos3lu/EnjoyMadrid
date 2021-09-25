package com.enjoymadrid.model.interfaces;

public class RouteInterfaces {
	
	private RouteInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface PointsData extends BasicData, PointInterfaces.BasicData {}
	
	public static interface UserData extends BasicData, UserInterfaces.BasicData {}

}
