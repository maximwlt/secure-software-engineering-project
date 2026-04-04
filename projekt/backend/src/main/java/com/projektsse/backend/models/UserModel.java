package com.projektsse.backend.models;


import com.projektsse.backend.controller.dto.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserModel(UUID id, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public UserProfileResponse toDto() {
        return new UserProfileResponse(id.toString(), email);
    }
}
