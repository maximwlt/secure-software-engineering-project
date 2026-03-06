package com.projektsse.backend.controller.dto;

import com.projektsse.backend.interfaces.RegistrationValidationGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user login requests. Validates email and password fields with specific constraints.
 * GroupSequence ensures that validation is performed in the aligned order.
 * @param email must not be blank, must be a valid email format, and must be at most 255 characters long.
 * @param password no specific validation constraints are applied to the password field
 */
@GroupSequence({
        LoginReq.class,
        RegistrationValidationGroups.EmailSize.class,
        RegistrationValidationGroups.EmailFormat.class
})
public record LoginReq(
        @NotBlank(message = "Email cannot be empty")
        @Size(max = 255, message = "Email must be at most 255 characters long")
        @Email(message = "Invalid email format")
        String email,

        String password
) { }
