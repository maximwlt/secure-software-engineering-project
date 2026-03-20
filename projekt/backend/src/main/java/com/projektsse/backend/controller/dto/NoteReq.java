package com.projektsse.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new note.
 * @param title Is the title of the note, must not be blank and has a maximum length of 255 characters.
 * @param mdContent Is the Markdown content of the note, must not be blank and has a maximum length of 10,000 characters.
 * @param isPrivate Indicates whether the note is private or public, must not be null.
 */
public record NoteReq(
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title can be at most 100 characters long")
    String title,
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 10000, message = "Content can be at most 10,000 characters long")
    String mdContent,
    @NotNull(message = "Visibility has to be specified")
    boolean isPrivate
) { }
