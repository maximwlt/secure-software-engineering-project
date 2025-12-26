package com.projektsse.backend.service;

import com.projektsse.backend.exceptions.UnauthorizedExceptionCustom;
import com.projektsse.backend.exceptions.UserNotFoundException;
import com.projektsse.backend.repository.RefreshTokenRepository;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.RefreshToken;
import com.projektsse.backend.repository.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private static final SecureRandom secRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenHasher refreshTokenHasher;

    public TokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, RefreshTokenHasher refreshTokenHasher) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    public String generateOpaqueToken() {
        byte[] randomBytes = new byte[32];
        secRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public String createRefreshToken(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        String token = generateOpaqueToken();
        String hashedToken = refreshTokenHasher.hash(token);
        Instant expiresAt = Instant.now().plus(Duration.ofDays(7));
        RefreshToken refreshToken = new RefreshToken(
                hashedToken, expiresAt, user.get()
        );
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public void deleteRefreshToken(String rawToken) {
        String hash = refreshTokenHasher.hash(rawToken);
        RefreshToken token = refreshTokenRepository.findRefreshTokenByTokenAndExpiresAtAfter(hash, Instant.now())
                .orElseThrow(() -> new UnauthorizedExceptionCustom("Invalid refresh token"));
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public String rotateRefreshToken(String oldToken) {
        String oldTokenHash = refreshTokenHasher.hash(oldToken);
        RefreshToken token = refreshTokenRepository.findRefreshTokenByTokenAndExpiresAtAfter(oldTokenHash, Instant.now())
                .orElseThrow(() -> new UnauthorizedExceptionCustom("Invalid refresh token"));

        refreshTokenRepository.delete(token);

        String newRefreshToken = createRefreshToken(token.getUserId().toString());

        return newRefreshToken;
    }

    public Optional<String> validateRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = refreshTokenHasher.hash(rawToken);
        Instant current_time = Instant.now();
        Optional<RefreshToken> storedToken = refreshTokenRepository.findRefreshTokenByTokenAndExpiresAtAfter(tokenHash, current_time);

        if (storedToken.isEmpty()) {
            return Optional.empty();
        } else {
            UUID userId = storedToken.get().getUserId();
            return Optional.of(userId.toString());
        }
    }

}
