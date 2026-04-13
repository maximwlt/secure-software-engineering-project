package com.projektsse.backend.feature.auth.api.dto;

import com.projektsse.backend.feature.auth.repository.entities.User;

import java.time.Instant;

public record PWResetTokenResponse(
        String token,
        Instant expiresAt,
        Instant createdAt,
        User user
) {
}
