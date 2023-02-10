package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.controllertests.utils.MockAuthentication;
import com.example.enjoymadrid.models.RefreshToken;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;
import com.example.enjoymadrid.security.jwt.JwtUtilityToken;
import com.example.enjoymadrid.services.RefreshTokenService;
import com.example.enjoymadrid.services.UserService;
import com.example.enjoymadrid.servicesimpl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class AuthServiceTests {
	
	@Mock
	private RefreshTokenService refreshTokenService;
	
	@Mock
	private UserService userService;
		
	@Mock
	private AuthenticationManager authenticationManager;
		
	@Mock
	private JwtUtilityToken jwtUtilityToken;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	public void signIn() throws Exception {
		User expectedUser = new User("Sam", "SamSmith", "12345ABCdef");
		expectedUser.setId(1L);
		
		SignInRequestDto signInRequestDto = new SignInRequestDto();
		signInRequestDto.setUsername("SamSmith");
		signInRequestDto.setPassword("12345ABCdef");
		
		RefreshToken expectedRefreshToken = new RefreshToken(expectedUser, 
				"mockRefreshToken", Instant.now().plusSeconds(43200));
		expectedRefreshToken.setId(2L);
		
		when(userService.getUserByUsername(anyString())).thenReturn(expectedUser);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenReturn(new MockAuthentication());
		when(jwtUtilityToken.generateToken(any(Authentication.class))).thenReturn("mockToken");
		when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(expectedRefreshToken);
		
		SignInResponseDto signInResponseDto = authService.signIn(signInRequestDto);
		assertThat(signInResponseDto.getName()).isEqualTo("Sam");
		assertThat(signInResponseDto.getUsername()).isEqualTo("SamSmith");
		assertThat(signInResponseDto.getToken()).isEqualTo("mockToken");
		assertThat(signInResponseDto.getRefreshToken()).isEqualTo("mockRefreshToken");
		verify(userService).getUserByUsername("SamSmith");
		verify(refreshTokenService).createRefreshToken(1L);
	}
	
	@Test
	public void signOut() {		
		assertDoesNotThrow(
				() -> authService.signOut(request, response));
	}
	
	@Test
	public void refreshToken() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(1L);
		RefreshToken refreshToken = new RefreshToken(user, "mockRefreshToken", Instant.now());
		refreshToken.setId(2L);
		
		RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
		refreshTokenRequestDto.setRefreshToken("mockRefreshToken");
		
		when(refreshTokenService.findByRefreshToken(anyString()))
			.thenReturn(refreshToken);
		when(jwtUtilityToken.generateTokenFromUsername(anyString()))
			.thenReturn("mockToken");
		
		RefreshTokenResponseDto refreshTokenResponseDto = authService.refreshToken(refreshTokenRequestDto);
		assertThat(refreshTokenResponseDto.getAccessToken()).isEqualTo("mockToken");
		assertThat(refreshTokenResponseDto.getRefreshToken()).isEqualTo("mockRefreshToken");
		verify(refreshTokenService).findByRefreshToken("mockRefreshToken");
		verify(jwtUtilityToken).generateTokenFromUsername("SamSmith");
	}
	
}
