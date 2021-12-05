package com.enjoymadrid.models.interfaces;

public class UserInterfaces {
	
	private UserInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface UsernameData extends BasicData {} 
		
	public static interface PictureData {}
	
	public static interface PictureExtendData extends BasicData, PictureData {}
	
	public static interface ExtendData extends UsernameData, PictureData {}
		
	public static interface RouteData extends RouteInterfaces.BasicData {}

}