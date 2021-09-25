package com.enjoymadrid.model.interfaces;

public class UserInterfaces {
	
	private UserInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface RoutesData extends BasicData, RouteInterfaces.BasicData {}

}
