package com.enjoymadrid.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
		
}