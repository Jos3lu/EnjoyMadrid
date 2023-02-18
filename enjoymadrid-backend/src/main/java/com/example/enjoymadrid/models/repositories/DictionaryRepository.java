package com.example.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.Dictionary;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
		
	Optional<Dictionary> findByTerm(String term);
	
}
