package com.example.enjoymadrid.models.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.enjoymadrid.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String token);
	
	@Query("DELETE FROM RefreshToken t WHERE t.expiryDate <= :now")
	void deleteByExpiryDateLessThan(@Param("now") Instant now);
		
}
