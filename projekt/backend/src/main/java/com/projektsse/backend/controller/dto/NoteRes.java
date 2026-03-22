package com.projektsse.backend.controller.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record NoteRes(
    String title,
    String md_content,
    boolean is_private,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    UUID userId
) {}