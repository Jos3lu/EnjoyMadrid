package com.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.Point;

@Service
public interface PointService {

	public Point getPoint(Long id);
	
}
