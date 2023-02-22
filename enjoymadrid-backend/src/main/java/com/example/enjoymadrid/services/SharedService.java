package com.example.enjoymadrid.services;

public interface SharedService {

	/**
	 * Try to parse to Double if not possible then return null
	 * 
	 * @param parseString String to parse to Double
	 * @return Double or null if not possible
	 */
	public Double tryParseDouble(String parseString);
	
	/**
	 * Try to parse to Integer if not possible then return null
	 * 
	 * @param parseString String to parse to Integer 
	 * @return Integer or null if not possible
	 */
	public Integer tryParseInteger(String parseString);
	
	/**
	 * Calculate distance between two points on Earth
	 * 
	 * @param lat1/lon1 Start point latitude/longitude
	 * @param lat2/lat2 End point latitude/longitude
	 * @return Distance in kilometers
	 */
	public double haversine(double lat1, double lon1, double lat2, double lon2);
	
}
