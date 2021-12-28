package com.enjoymadrid.serviceslogic;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import com.enjoymadrid.models.repositories.RefreshTokenRepository;
import com.enjoymadrid.models.repositories.UserRepository;
import com.enjoymadrid.models.RefreshToken;
import com.enjoymadrid.services.RefreshTokenService;

@Service
public class RefreshTokenServiceLogic implements RefreshTokenService {

	private static final int JWT_REFRESH_EXPIRATION_MS = 12 * 60 * 60 * 1000; // 12 hour

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	public RefreshTokenServiceLogic(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}

	@Override
	public RefreshToken findByRefreshToken(String refreshToken) {
		return this.refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Autenticación no posible!"));
	}

	@Override
	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken(userRepository.findById(userId).get(),
				UUID.randomUUID().toString(), Instant.now().plusMillis(JWT_REFRESH_EXPIRATION_MS));

		return this.refreshTokenRepository.save(refreshToken);
	}

	@Override
	public void verifyExpiration(RefreshToken refreshToken) {
		if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
			this.refreshTokenRepository.delete(refreshToken);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Autenticación caducada. Por favor, vuelve a iniciar sesión!");
		}
	}

}
