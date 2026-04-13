package com.projektsse.backend.feature.auth.components;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieFactory {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    private String FINGERPRINT_COOKIE_NAME;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @PostConstruct
    private void init() {
        this.FINGERPRINT_COOKIE_NAME = cookieSecure ? "__Secure-Fgp" : "Fgp"; // Runs after @Value injection
    }


    public ResponseCookie setRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth/rt")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie setFingerprintCookie(String fingerprint, Duration accessTokenDuration) {
        return ResponseCookie.from(FINGERPRINT_COOKIE_NAME, fingerprint)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(accessTokenDuration) // Max-Age <= JWT Expiry
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth/rt")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie deleteFingerprintCookie() {
        return ResponseCookie.from(FINGERPRINT_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
