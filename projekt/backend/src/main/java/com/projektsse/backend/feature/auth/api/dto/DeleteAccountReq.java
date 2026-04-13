package com.projektsse.backend.feature.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for deleting a user account. It requires the user's password to confirm the deletion.
 * @param password the password of the account to be deleted, must not be blank
 */
public record DeleteAccountReq(
        @NotBlank(message = "Password is required to delete the account.")
        String password
) { }
