package com.projektsse.backend.interfaces;

/**
 * Marker interface for grouping validation constraints during user registration.
 * This allows for more granular control over which validation rules are applied when validating user input during the registration process.
 */
public interface RegistrationValidationGroups {
    interface EmailSize { }
    interface EmailFormat { }
    interface PasswordValidation { }
}
