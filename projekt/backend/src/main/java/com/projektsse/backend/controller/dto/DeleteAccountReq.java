package com.projektsse.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountReq(
        @NotBlank(message = "Passwort ist erforderlich.")
        String password
) { }
