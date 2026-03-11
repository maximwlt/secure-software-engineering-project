package com.projektsse.backend.repository.entities;

import com.projektsse.backend.models.PWResetTokenResponse;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a password reset token. Each token is associated with a user and has an expiration time.
 * The token is stored as a hash for security reasons. Once used, the token's usedAt field is set to prevent reuse.
 */
@Entity
@Table(name = "pw_reset_tokens")
public class PWResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name="expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name="used_at")
    private Instant usedAt;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PWResetToken() {}

    public PWResetToken(String tokenHash, Instant expiresAt, Instant createdAt, User user) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = null;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }

    public PWResetTokenResponse toModel() {
        return new PWResetTokenResponse(
                tokenHash,
                expiresAt,
                createdAt,
                user
        );
    }

}

