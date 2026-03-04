package com.projektsse.backend.controller.dto;

import jakarta.validation.constraints.Email;

public record EmailPasswordReset(
        @Email String email
) {
}
