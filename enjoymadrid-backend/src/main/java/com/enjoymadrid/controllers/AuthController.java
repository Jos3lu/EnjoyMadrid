package com.enjoymadrid.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.dtos.SignInRequestDto;
import com.enjoymadrid.model.dtos.SignInResponseDto;
import com.enjoymadrid.security.jwt.JwtUtilityToken;

@RestController
@RequestMapping("/api")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	private final JwtUtilityToken jwtUtilityToken;
	
	public AuthController(AuthenticationManager authenticationManager, JwtUtilityToken jwtUtilityToken) {
		this.authenticationManager = authenticationManager;
		this.jwtUtilityToken = jwtUtilityToken;
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInResponseDto> signIn(@Valid @RequestBody SignInRequestDto loginDto) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtUtilityToken.generateToken(auth);
		return ResponseEntity.ok(new SignInResponseDto(jwtToken));
	}
	
	@GetMapping("/signout")
	public ResponseEntity<Void> SignOut(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
}
