package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.NoteReq;
import com.projektsse.backend.interfaces.CurrentUserId;
import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class NotesController {

    NoteService noteService;

    NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<?> getNotes() {
        List<NoteModel> notes = noteService.getAllPublicNotes();
        return ResponseEntity.ok().body(notes);
    }

//    @GetMapping
//    public ResponseEntity<?> getNoteById(@RequestParam("id") String id) {
//        // Implementation for fetching a note by its ID
//    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> createNote(@Valid @RequestBody NoteReq noteReq, @CurrentUserId UUID userId) {
        NoteModel note = noteService.createNote(noteReq, userId.toString());
        return ResponseEntity.status(201).body(note); // TODO: Fix this XSS issue by nginx configuration or Spring settings in SecurityConfig

}
