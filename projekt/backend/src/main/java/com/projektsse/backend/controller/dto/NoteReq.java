package com.projektsse.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteReq(
    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 255, message = "Titel darf maximal 255 Zeichen lang sein")
    String title,
    @NotBlank(message = "Inhalt darf nicht leer sein")
    @Size(max = 10000, message = "Inhalt darf maximal 10.000 Zeichen lang sein")
    String mdContent,
    @NotNull(message = "Sichtbarkeit muss angegeben werden")
    boolean isPrivate
//    @NotBlank(message = "UserId darf nicht leer sein")
//    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Ungültiges Format")
//    String userId
) {
}
