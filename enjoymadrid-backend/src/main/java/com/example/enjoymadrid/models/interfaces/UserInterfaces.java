package com.example.enjoymadrid.models.interfaces;

public class UserInterfaces {
	
	private UserInterfaces() {}
	
	public static interface BasicData {}
			
	public static interface PictureData {}
		
	public static interface UserData extends BasicData {}
	
	public static interface RouteData extends RouteInterfaces.RouteData {}

}
