package com.projektsse.backend.controller.dto;

import com.projektsse.backend.interfaces.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
        @NotBlank
        String token,

        @StrongPassword
        String newPassword
) {
}
