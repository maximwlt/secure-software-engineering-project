package com.projektsse.backend.feature.auth.repository.entities;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import java.time.Instant;

@Entity
@Table(name = "registration_requests")
public class Registration_Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable = false, length = 255)
    private String email;

    @Column(name="password_hash", nullable = false, length = 255)
    private String password_hash;

    @Column(name="verification_code", unique = true)
    private String verificationCode;

    @Column(name="expires_at")
    private Instant verificationCodeExpiry;

    public Registration_Request(String email, @Nullable String encode, String verificationCode, Instant plus) {
        this.email = email;
        this.password_hash = encode;
        this.verificationCode = verificationCode;
        this.verificationCodeExpiry = plus;
    }

    public Registration_Request() {}

    public String getEmail() {
        return email;
    }

    public String getPassword_hash() {
        return password_hash;
    }
}
