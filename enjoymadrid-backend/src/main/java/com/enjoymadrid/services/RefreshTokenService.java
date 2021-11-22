package com.enjoymadrid.services;

import org.springframework.stereotype.Service;

import com.enjoymadrid.models.RefreshToken;

@Service
public interface RefreshTokenService {

	public RefreshToken findByRefreshToken(String token);
	
	public RefreshToken createRefreshToken(Long userId);
	
	public void verifyExpiration(RefreshToken token);
	
}
