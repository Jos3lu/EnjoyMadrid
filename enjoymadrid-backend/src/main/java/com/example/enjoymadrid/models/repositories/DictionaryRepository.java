package com.example.enjoymadrid.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
	
	@Query("SELECT d FROM Dictionary d JOIN FETCH d.weights p WHERE KEY(p) = :touristicPoint")
	List<Dictionary> findByWeightsTouristicPoint(TouristicPoint touristicPoint);

}
