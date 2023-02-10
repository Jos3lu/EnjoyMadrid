package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.RefreshToken;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.repositories.RefreshTokenRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.servicesimpl.RefreshTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class RefreshTokenServiceTests {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private RefreshTokenServiceImpl refreshTokenService;
	
	private User user;
	private RefreshToken refreshToken;
	
	@BeforeEach
	public void setUp() {
		user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(1L);
		refreshToken = new RefreshToken(user, "mockRefreshToken", Instant.now().plusSeconds(100));
		refreshToken.setId(2L);
	}
	
	@Test
	public void findByRefreshToken() {
		when(refreshTokenRepository.findByRefreshToken(anyString()))
			.thenReturn(Optional.of(refreshToken));
		
		RefreshToken refreshTokenResult = refreshTokenService.findByRefreshToken("mockRefreshToken");
		
		assertThat(refreshTokenResult).isEqualTo(refreshToken);
		verify(refreshTokenRepository).findByRefreshToken("mockRefreshToken");
	}
	
	@Test
	public void findByRefreshToken_exception() {
		// Refresh token not found
		when(refreshTokenRepository.findByRefreshToken(anyString()))
			.thenReturn(Optional.empty());
		
		assertThrows(ResponseStatusException.class, 
				() -> refreshTokenService.findByRefreshToken("mockRefreshToken"));
		verify(refreshTokenRepository).findByRefreshToken("mockRefreshToken");
	}
	
	@Test
	public void createRefreshToken() {		
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
		
		RefreshToken refreshTokenResult = refreshTokenService.createRefreshToken(1L);
		assertThat(refreshTokenResult).isEqualTo(refreshToken);
		verify(userRepository).findById(1L);
		verify(refreshTokenRepository).save(any(RefreshToken.class));
	}
	
	@Test
	public void createRefreshToken_exception() {
		// User not found
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class, 
				() -> refreshTokenService.createRefreshToken(1L));
		verify(userRepository).findById(1L);
	}
	
	@Test
	public void verifyExpiration() {
		assertDoesNotThrow(
				() -> refreshTokenService.verifyExpiration(refreshToken));
		verify(refreshTokenRepository, times(0)).delete(refreshToken);
	}
	
	@Test
	public void verifyExpiration_exception() {
		// Refresh token is expired
		refreshToken.setExpiryDate(refreshToken.getExpiryDate().minusSeconds(300));
		assertThrows(ResponseStatusException.class, 
				() -> refreshTokenService.verifyExpiration(refreshToken));
		verify(refreshTokenRepository).delete(refreshToken);
	}
	
	@Test
	public void purgeExpiredTokens() {
		assertDoesNotThrow(
				() -> refreshTokenService.purgeExpiredTokens());
		verify(refreshTokenRepository).deleteByExpiryDateLessThan(any(Instant.class));
	}
	
}
