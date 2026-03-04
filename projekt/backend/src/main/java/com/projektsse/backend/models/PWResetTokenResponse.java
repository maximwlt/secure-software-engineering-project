package com.projektsse.backend.models;

import com.projektsse.backend.repository.entities.User;

import java.time.Instant;

public record PWResetTokenResponse(
        String token,
        Instant expiresAt,
        Instant createdAt,
        User user
) {
}
