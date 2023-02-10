package com.example.enjoymadrid.services;

import org.springframework.http.HttpStatus;

import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	
	/**
	 * User login
	 * 
	 * @param loginDto Data with username & password of user
	 * @return User information
	 */
	public SignInResponseDto signIn(SignInRequestDto loginDto);
	
	/**
	 * User logout
	 * 
	 * @param request Provide request information for HTTP servlets
	 * @param response Provide HTTP-specific functionality in sending a response
	 * @return Http status
	 */
	public HttpStatus signOut(HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * Check if refresh token is expired
	 * 
	 * @param refreshTokenRequest Refresh token
	 * @return If not expired refresh token: new access token & refresh token
	 */
	public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);

}
