package com.example.enjoymadrid.models.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
		
	Boolean existsByUsername(String username);
	
	@Query("SELECT u FROM User u JOIN FETCH u.touristicPoints p WHERE p = :touristicPoint")
	Set<User> findByTouristicPoints(TouristicPoint touristicPoint);
	
}