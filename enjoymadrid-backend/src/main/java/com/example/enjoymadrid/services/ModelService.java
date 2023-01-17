package com.example.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;

@Service
public interface ModelService {

	public List<Dictionary> rankDocuments();
	
}
