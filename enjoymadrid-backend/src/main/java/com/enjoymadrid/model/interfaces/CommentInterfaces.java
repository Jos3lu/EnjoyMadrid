package com.enjoymadrid.model.interfaces;

public class CommentInterfaces {
	
	private CommentInterfaces() {}
	
	public static interface BasicData {}
	
	public static interface UserData extends BasicData, UserInterfaces.PictureData {}
	
	public static interface PointData extends BasicData, PointInterfaces.BasicData {}
	
}
