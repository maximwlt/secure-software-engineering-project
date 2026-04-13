package com.projektsse.backend.feature.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;

@Service
public class JwtService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private int accessTokenExpiration; // In Minuten

    private final Logger log = LoggerFactory.getLogger(JwtService.class);

    private Algorithm getAlgorithm() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Algorithm.HMAC256(keyBytes);
    }

    /**
     * Generiert einen zufälligen Fingerprint (50 Bytes, Hex-kodiert)
     */
    public String generateFingerprint() {
        byte[] randomBytes = new byte[50];
        SECURE_RANDOM.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }

    /**
     * Berechnet SHA3-256 Hash des Fingerprints
     */
    public String hashFingerprint(String fingerprint) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hashBytes = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA3-256 nicht verfügbar", e);
        }
    }

    /**
     * Generiert Access Token mit Fingerprint-Hash im Claim
     */
    public String generateAccessToken(String userId, String fingerprintHash) {
        Instant now = Instant.now();
        return JWT.create()
                  .withSubject(userId)
                  .withIssuedAt(now)
                  .withExpiresAt(now.plus(accessTokenExpiration, ChronoUnit.MINUTES))
                  .withNotBefore(now)
                  .withClaim("fgp", fingerprintHash) // Fingerprint-Hash
                  .sign(getAlgorithm());
    }

    /**
     * Verifiziert Token mit Fingerprint aus Cookie
     */
    public DecodedJWT verifyTokenWithFingerprint(String token, String fingerprintFromCookie) {
        String fingerprintHash = hashFingerprint(fingerprintFromCookie);

        JWTVerifier verifier = JWT.require(getAlgorithm())
                                  .withClaim("fgp", fingerprintHash)
                                  .build();
        return verifier.verify(token);
    }

    public String extractUserId(String token) {
        return verifyAndDecode(token).getSubject();
    }


    private DecodedJWT verifyAndDecode(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

    public boolean isTokenValid(String token, String fingerprintFromCookie, UserDetails userDetails) {
        try {
            DecodedJWT decoded = verifyTokenWithFingerprint(token, fingerprintFromCookie);
            String userId = decoded.getSubject();
            boolean isExpired = decoded.getExpiresAt().before(new Date());
            return userId.equals(userDetails.getUsername()) && !isExpired;
        } catch (AlgorithmMismatchException e) {
            log.warn("Invalid JWT algorithm: {}", e.getMessage());
            return false;
        } catch (SignatureVerificationException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (TokenExpiredException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (MissingClaimException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (IncorrectClaimException e) {
            log.warn("Invalid JWT claim: {}", e.getMessage());
            return false;
        }
    }

    public int getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
