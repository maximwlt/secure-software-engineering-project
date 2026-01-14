package com.projektsse.backend.controller.dto;

import com.projektsse.backend.interfaces.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterReq(
        @NotBlank(message = "Email darf nicht leer sein")
        @Size(max = 255, message = "E-Mail-Adresse darf maximal 255 Zeichen lang sein")
        @Email(message = "Ungültige E-Mail-Adresse")
        String email,

        @NotBlank(message = "Passwort darf nicht leer sein")
        @StrongPassword
        String password
) {
}
