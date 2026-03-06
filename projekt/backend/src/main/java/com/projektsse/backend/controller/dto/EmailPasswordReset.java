package com.projektsse.backend.controller.dto;

import com.projektsse.backend.interfaces.RegistrationValidationGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for requesting a password reset via email.
 * The GroupSequence annotation ensures that the email field is validated in a specific order:
 * first it checks if it's not blank, then it checks the size, and finally it checks the email format.
 * @param email the email address of the user requesting the password reset, must not be blank, must be a valid email format, and must be at most 255 characters long
 */
@GroupSequence({
        EmailPasswordReset.class,
        RegistrationValidationGroups.EmailSize.class,
        RegistrationValidationGroups.EmailFormat.class
})
public record EmailPasswordReset(
        @NotBlank(message = "Email cannot be empty")
        @Size(max = 255, message = "Email must be at most 255 characters long")
        @Email(message = "Invalid email format")
        String email
) { }
