package com.projektsse.backend.service;

import com.projektsse.backend.repository.RefreshTokenRepository;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.RefreshToken;
import com.projektsse.backend.repository.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private static final SecureRandom secRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenService(PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public String generateOpaqueToken() {
        byte[] randomBytes = new byte[32];
        secRandom.nextBytes(randomBytes);
        return base64Encoder.withoutPadding().encodeToString(randomBytes);
    }

    public String createRefreshToken(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        String token = generateOpaqueToken();
        String hashedToken = passwordEncoder.encode(token);
        // Long expiresAt = System.currentTimeMillis() + refreshTokenExpiration;
        long expiresAt = Instant.now().plus(Duration.ofDays(7)).toEpochMilli();
        RefreshToken refreshToken = new RefreshToken(
                hashedToken, expiresAt, user.get()
        );
        refreshTokenRepository.save(refreshToken);
        return token;
    }

//    private boolean isTokenExpired(String hashedToken) {
//        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken).orElse(null);
//        if (refreshToken == null) {
//            return true;
//        }
//        return refreshToken.getExpiresAt() < System.currentTimeMillis();
//    }

    public void deleteRefreshToken(String rawToken) {
        List<RefreshToken> allTokens = refreshTokenRepository.findAllByExpiresAtGreaterThan(System.currentTimeMillis());

        for (RefreshToken refreshToken : allTokens) {
            if (passwordEncoder.matches(rawToken, refreshToken.getToken())) {
                refreshTokenRepository.delete(refreshToken);
                return;
            }
        }
    }

    public String rotateRefreshToken(String oldToken, String userId) {
        deleteRefreshToken(oldToken);
        return createRefreshToken(userId);
    }

    public Optional<String> validateRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        // Alle nicht-abgelaufenen Tokens holen und gegen den Hash prüfen
        List<RefreshToken> allTokens = refreshTokenRepository.findAllByExpiresAtGreaterThan(System.currentTimeMillis());

        for (RefreshToken refreshToken : allTokens) {
            if (passwordEncoder.matches(rawToken, refreshToken.getToken())) {
                return Optional.of(refreshToken.getUser().getId().toString());
            }
        }
        return Optional.empty();
    }

}
