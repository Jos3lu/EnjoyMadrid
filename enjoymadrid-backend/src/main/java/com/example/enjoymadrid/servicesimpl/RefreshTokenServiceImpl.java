package com.example.enjoymadrid.servicesimpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.RefreshToken;
import com.example.enjoymadrid.models.repositories.RefreshTokenRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private static final int JWT_REFRESH_EXPIRATION_MS = 12 * 60 * 60 * 1000; // 12 hour

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
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
		RefreshToken refreshToken = new RefreshToken(this.userRepository.findById(userId).get(),
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

	@Override
	public void purgeExpiredTokens() {
		Instant now = Instant.now();
		this.refreshTokenRepository.deleteByExpiryDateLessThan(now);
	}

}
