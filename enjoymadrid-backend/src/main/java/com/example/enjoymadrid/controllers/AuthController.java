package com.example.enjoymadrid.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;
import com.example.enjoymadrid.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInResponseDto> signIn(@Valid @RequestBody SignInRequestDto loginDto) {
		return ResponseEntity.ok(this.authService.signIn(loginDto));
	}

	@PostMapping("/signout")
	public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
		return new ResponseEntity<>(this.authService.signOut(request, response));
	}
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<RefreshTokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
		return ResponseEntity.ok(this.authService.refreshToken(refreshTokenRequest));
	}

}
