package com.projektsse.backend.controller.dto;

import com.projektsse.backend.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank(message = "Email darf nicht leer sein")
        @Email(message = "Ungültige E-Mail-Adresse")
        String email,

        @NotBlank(message = "Passwort darf nicht leer sein")
        @StrongPassword
        String password
) {
}
