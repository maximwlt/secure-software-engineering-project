package com.projektsse.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration; // In Minuten

    JwtService() { }

    private Algorithm getAlgorithm() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Algorithm.HMAC256(keyBytes);
    }

    public String generateAccessToken(String userId) {
        Instant now = Instant.now();
        return JWT.create()
                  .withSubject(userId)
                  .withIssuedAt(now)
                  .withExpiresAt(now.plus(accessTokenExpiration, ChronoUnit.MINUTES))
                  .withNotBefore(now)
                  .sign(getAlgorithm());
    }

    public String extractUsername(String token) {
        return verifyAndDecode(token).getSubject();
    }

    public String extractUserId(String token) {
        return extractUsername(token);
    }

    public Date extractExpiration(String token) {
        return verifyAndDecode(token).getExpiresAt();
    }

    private DecodedJWT verifyAndDecode(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm())
                                  .build();
        return verifier.verify(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
