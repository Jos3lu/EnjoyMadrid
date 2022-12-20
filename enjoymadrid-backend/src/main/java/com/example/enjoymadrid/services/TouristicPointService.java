package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;

@Service
public interface TouristicPointService {

	public List<TouristicPoint> getTouristicPointsByCategory(String category);
	
	public List<TouristicPoint> getUserTouristicPoints(Long userId);
	
	public void addTouristicPointToUser(Long userId, Long touristPointId);
	
	public void deleteUserTouristicPoint(Long userId, Long touristPointId);
	
}
