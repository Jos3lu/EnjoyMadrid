package com.example.enjoymadrid.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.enjoymadrid.models.TermWeight;

public interface DictionaryRepository extends JpaRepository<TermWeight, Long> {
		
	@Query("SELECT t FROM TermWeight t JOIN FETCH t.weights WHERE t.term = :term")
	Optional<TermWeight> findByTerm(String term);
	
}
