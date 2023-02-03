package com.example.enjoymadrid.models.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enjoymadrid.models.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	
	@Query("DELETE FROM RefreshToken t WHERE t.expiryDate <= :now")
	void deleteByExpiryDateLessThan(@Param("now") Instant now);
		
}
