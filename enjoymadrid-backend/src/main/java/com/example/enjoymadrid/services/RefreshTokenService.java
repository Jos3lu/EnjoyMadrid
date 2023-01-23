package com.example.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.RefreshToken;

@Service
public interface RefreshTokenService {

	/**
	 * Get information associated to a refresh token
	 * 
	 * @param token Refresh token
	 * @return Information related to the refresh token
	 */
	public RefreshToken findByRefreshToken(String token);
	
	/**
	 * Create a new refresh token by user ID
	 * 
	 * @param userId The ID of the user
	 * @return A refresh token
	 */
	public RefreshToken createRefreshToken(Long userId);
	
	/**
	 * Check if refresh token is expired
	 * 
	 * @param token Refresh token
	 */
	public void verifyExpiration(RefreshToken token);
	
	/**
	 * Remove expired refresh tokens from the DB
	 */
	public void purgeExpiredTokens();
	
}
