package com.enjoymadrid.model.interfaces;

public class PointInterfaces {
	
	private PointInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface RouteData extends BasicData, RouteInterfaces.BasicData {}

}
