package com.projektsse.backend.feature.auth.api.dto;

import com.projektsse.backend.shared.interfaces.RegistrationValidationGroups;
import com.projektsse.backend.shared.interfaces.StrongPasswordEmailCheck;
import com.projektsse.backend.feature.auth.model.UserReqModel;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests. Validates email and password fields with specific constraints.
 * @param email must not be blank, must be a valid email format, and must be at most 255 characters long.
 * @param password must be validated by the StrongPasswordValidator, which checks for password policy
 */
@StrongPasswordEmailCheck(groups = RegistrationValidationGroups.PasswordValidation.class)
@GroupSequence({
        RegisterRequest.class,
        RegistrationValidationGroups.EmailSize.class,
        RegistrationValidationGroups.EmailFormat.class,
        RegistrationValidationGroups.PasswordValidation.class
})
public record RegisterRequest(
        @NotBlank(message = "Email must not be empty")
        @Size(max = 255, message = "Email must be at most 255 characters long", groups = RegistrationValidationGroups.EmailSize.class)
        @Email(message = "Invalid email format", groups = RegistrationValidationGroups.EmailFormat.class)
        String email,

        String password
) {
        public UserReqModel toModel() {
                return new UserReqModel(email, password);
        }
}
