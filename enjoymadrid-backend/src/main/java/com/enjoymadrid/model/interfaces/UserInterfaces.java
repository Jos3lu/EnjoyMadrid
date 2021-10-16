package com.enjoymadrid.model.interfaces;

public class UserInterfaces {
	
	private UserInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface EmailData extends BasicData {} 
	
	public static interface PictureData extends BasicData {}
	
	public static interface ExtendData extends EmailData, PictureData {}
	
	public static interface CommentData extends CommentInterfaces.PointData {}
	
	public static interface RouteData extends RouteInterfaces.BasicData {}

}
