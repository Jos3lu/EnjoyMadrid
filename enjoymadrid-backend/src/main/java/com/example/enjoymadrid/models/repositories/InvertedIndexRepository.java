package com.example.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.InvertedIndex;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {

}
