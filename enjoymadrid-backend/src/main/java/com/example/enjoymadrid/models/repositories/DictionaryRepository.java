package com.example.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.TermWeight;

public interface DictionaryRepository extends JpaRepository<TermWeight, Long> {
	
	Optional<TermWeight> findTopByTerm(String term);
	
}
