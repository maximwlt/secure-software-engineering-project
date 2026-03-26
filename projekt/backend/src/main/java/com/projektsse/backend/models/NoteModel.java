package com.projektsse.backend.models;

import com.projektsse.backend.controller.dto.NoteResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteModel(
    UUID noteId,
    String title,
    String md_content,
    boolean is_private,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    UUID userId
) {
    public NoteResponse toDto() {
        return new NoteResponse(noteId, title, md_content, is_private, created_at, updated_at, userId);
    }


    public NoteModel setTitle(String title) {
        return new NoteModel(noteId, title, md_content, is_private, created_at, updated_at, userId);
    }

    public NoteModel setMdContent(String md_content) {
        return new NoteModel(noteId, title, md_content, is_private, created_at, updated_at, userId);
    }

    public NoteModel setIsPrivate(boolean is_private) {
        return new NoteModel(noteId, title, md_content, is_private, created_at, updated_at, userId);
    }
}
