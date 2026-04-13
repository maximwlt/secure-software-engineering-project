package com.projektsse.backend.feature.auth.api.dto;

import com.projektsse.backend.shared.interfaces.RegistrationValidationGroups;
import com.projektsse.backend.shared.interfaces.StrongPassword;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password reset requests. Validates the token and new password fields with specific constraints.
 * The GroupSequence annotation ensures that the token is validated first, followed by the new password.
 * @param token
 * @param newPassword
 */
@GroupSequence({PasswordResetRequest.class, RegistrationValidationGroups.PasswordValidation.class})
public record PasswordResetRequest(
        @NotBlank(message = "Token must not be blank")
        String token,

        @NotBlank(message = "New password must not be blank")
        @StrongPassword(groups = RegistrationValidationGroups.PasswordValidation.class)
        String newPassword
) { }
