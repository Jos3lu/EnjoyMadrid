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

import com.enjoymadrid.models.RefreshToken;
import com.enjoymadrid.models.User;
import com.enjoymadrid.models.dtos.RefreshTokenRequest;
import com.enjoymadrid.models.dtos.RefreshTokenResponse;
import com.enjoymadrid.models.dtos.SignInRequestDto;
import com.enjoymadrid.models.dtos.SignInResponseDto;
import com.enjoymadrid.security.jwt.JwtUtilityToken;
import com.enjoymadrid.services.RefreshTokenService;
import com.enjoymadrid.services.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtilityToken jwtUtilityToken;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtilityToken jwtUtilityToken,
			RefreshTokenService refreshTokenService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtilityToken = jwtUtilityToken;
		this.refreshTokenService = refreshTokenService;
		this.userService = userService;
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInResponseDto> signIn(@Valid @RequestBody SignInRequestDto loginDto) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtUtilityToken.generateToken(auth);
		User user = userService.getUserByUsername(loginDto.getUsername());
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
		return ResponseEntity.ok(new SignInResponseDto(jwtToken, refreshToken.getRefreshToken(), user.getId(),
				user.getName(), user.getUsername(), user.getPhoto()));
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
	
	@PostMapping("/refreshToken")
	public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		String refreshToken = refreshTokenRequest.getRefreshToken();
		
		RefreshToken refreshTokenClass = this.refreshTokenService.findByRefreshToken(refreshToken);
		this.refreshTokenService.verifyExpiration(refreshTokenClass);
		
		String username = refreshTokenClass.getUser().getUsername();
		String token = jwtUtilityToken.generateTokenFromUsername(username);
		
		return ResponseEntity.ok(new RefreshTokenResponse(token, refreshToken));
	}

}
