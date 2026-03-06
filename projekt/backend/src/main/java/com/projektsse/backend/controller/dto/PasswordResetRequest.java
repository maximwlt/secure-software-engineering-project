package com.projektsse.backend.controller.dto;

import com.projektsse.backend.interfaces.RegistrationValidationGroups;
import com.projektsse.backend.interfaces.StrongPassword;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password reset requests. Validates the token and new password fields with specific constraints.
 * The GroupSequence annotation ensures that the token is validated first, followed by the new password.
 * @param token
 * @param newPassword
 */
@StrongPassword(groups = RegistrationValidationGroups.PasswordValidation.class)
@GroupSequence({PasswordResetRequest.class, RegistrationValidationGroups.PasswordValidation.class})
public record PasswordResetRequest(
        @NotBlank(message = "Token must not be blank")
        String token,
        String newPassword
) { }
// Improvement: Adding email field