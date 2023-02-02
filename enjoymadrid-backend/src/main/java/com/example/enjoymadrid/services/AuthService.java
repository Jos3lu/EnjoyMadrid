package com.example.enjoymadrid.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;

public interface AuthService {
	
	public SignInResponseDto signIn(SignInRequestDto loginDto);
	
	public HttpStatus signOut(HttpServletRequest request, HttpServletResponse response);
	
	public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);

}
