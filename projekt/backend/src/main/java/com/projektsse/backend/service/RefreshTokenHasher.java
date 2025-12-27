package com.projektsse.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class RefreshTokenHasher {

    private static final String HMAC_ALGORITHM = "HmacSHA256"; // Alternativ: Hashfunktion aus der SHA-3 Familie: HmacSHA3-256
    private final SecretKeySpec secretKeySpec;

    public RefreshTokenHasher(@Value("${refreshToken.value}") String hmacSecret) {
        this.secretKeySpec = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    }

    public String hash(String refreshToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hashed = mac.doFinal(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("HMAC not initialized", e);
        }

    }

}
