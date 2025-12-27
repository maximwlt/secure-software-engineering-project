package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.NoteReq;
import com.projektsse.backend.interfaces.CurrentUserId;
import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@Validated
public class NotesController {

    NoteService noteService;

    NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/public")
    public ResponseEntity<?> getNotes() {
        List<NoteModel> notes = noteService.getAllPublicNotes();
        return ResponseEntity.ok().body(notes);
    }

    @GetMapping("/public/search")
    public ResponseEntity<?> searchPublicNotes(
            @RequestParam("q")
            @NotBlank
            @Size(max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9 äöüÄÖÜß!?.,-]*$", message = "Query contains invalid characters")
            String query
    ) {
        List<NoteModel> notes = noteService.searchPublicNotes(query);
        return ResponseEntity.ok().body(notes);
    }

    @GetMapping("/{documentId:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<?> getNoteById(@PathVariable UUID documentId) {
        NoteModel note = noteService.getNoteById(documentId);
        return ResponseEntity.ok().body(note);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserNotes(@CurrentUserId UUID userId) {
        List<NoteModel> notes = noteService.getNotesByUserId(userId);
        return ResponseEntity.ok().body(notes);
    }

    @GetMapping("/user/search")
    public ResponseEntity<?> searchUserNotes(
            @RequestParam("q")
            @NotBlank
            @Size(max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9 äöüÄÖÜß!?.,-]*$", message = "Query contains invalid characters")
            String query,
            @CurrentUserId UUID userId
    ) {
        List<NoteModel> notes = noteService.searchUserNotes(query, userId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> createNote(@Valid @RequestBody NoteReq noteReq, @CurrentUserId UUID userId) {
        NoteModel note = noteService.createNote(noteReq, userId.toString());
        return ResponseEntity.status(201).body(note); // TODO: Fix this XSS issue by nginx configuration or Spring settings in SecurityConfig
    }
}
