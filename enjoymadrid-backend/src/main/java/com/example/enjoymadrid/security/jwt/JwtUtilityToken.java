package com.example.enjoymadrid.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtilityToken {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtilityToken.class);
	private static final int JWT_EXPIRATION_TOKEN_MS = 1 * 60 * 60 * 1000; // 1 hour

	@Value("${enjoymadrid.jwt.secret}")
	private String jwtSecret;
	
	public String generateToken(Authentication auth) {
		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		return generateTokenFromUsername(userDetails.getUsername());
	}
	
	public String generateTokenFromUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_TOKEN_MS))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	public String getUsernameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean validateJwtToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			logger.warn("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.warn("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.warn("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.warn("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.warn("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
	
}
