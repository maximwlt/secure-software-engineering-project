package com.projektsse.backend.controller;

import com.projektsse.backend.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class NotesController {

    NoteService noteService;

    NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<?> getNotes() {
        return ResponseEntity.ok().body("Notes endpoint is working");
    }

}
