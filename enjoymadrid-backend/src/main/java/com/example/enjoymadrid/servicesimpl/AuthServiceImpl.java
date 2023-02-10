package com.example.enjoymadrid.servicesimpl;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.RefreshToken;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;
import com.example.enjoymadrid.security.jwt.JwtUtilityToken;
import com.example.enjoymadrid.services.AuthService;
import com.example.enjoymadrid.services.RefreshTokenService;
import com.example.enjoymadrid.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {
	
	private final AuthenticationManager authenticationManager;
	private final JwtUtilityToken jwtUtilityToken;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;
	
	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtilityToken jwtUtilityToken,
			RefreshTokenService refreshTokenService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtilityToken = jwtUtilityToken;
		this.refreshTokenService = refreshTokenService;
		this.userService = userService;
	}

	@Override
	public SignInResponseDto signIn(SignInRequestDto loginDto) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		String jwtToken = jwtUtilityToken.generateToken(auth);
		User user = userService.getUserByUsername(loginDto.getUsername());
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
		
		return new SignInResponseDto(jwtToken, refreshToken.getRefreshToken(), user.getId(),
				user.getName(), user.getUsername(), user.getPhoto(), user.getRoutes(), user.getTouristicPoints());
	}

	@Override
	public HttpStatus signOut(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			return HttpStatus.OK;
		}
		
		return HttpStatus.UNAUTHORIZED;
	}

	@Override
	public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest) {
		String refreshToken = refreshTokenRequest.getRefreshToken();
		
		RefreshToken refreshTokenClass = this.refreshTokenService.findByRefreshToken(refreshToken);
		this.refreshTokenService.verifyExpiration(refreshTokenClass);
		
		String username = refreshTokenClass.getUser().getUsername();
		String token = jwtUtilityToken.generateTokenFromUsername(username);
		
		return new RefreshTokenResponseDto(token, refreshToken);
	}

}
