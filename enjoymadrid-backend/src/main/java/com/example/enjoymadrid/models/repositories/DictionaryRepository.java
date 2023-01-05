package com.example.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enjoymadrid.models.Dictionary;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

}
