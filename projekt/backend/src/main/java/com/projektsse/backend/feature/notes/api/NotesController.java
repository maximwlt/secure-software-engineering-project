package com.projektsse.backend.feature.notes.api;

import com.projektsse.backend.feature.notes.api.dto.NoteRequest;
import com.projektsse.backend.feature.notes.api.dto.NoteResponse;
import com.projektsse.backend.shared.interfaces.CurrentUserId;
import com.projektsse.backend.feature.notes.model.NoteModel;
import com.projektsse.backend.feature.notes.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/documents")
public class NotesController {

    NoteService noteService;

    NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping(value = "/public", produces = "application/json")
    public ResponseEntity<List<NoteResponse>> getNotes() {
        List<NoteResponse> notes = noteService.getAllPublicNotes().stream().map(NoteModel::toDto).toList();
        return ResponseEntity.ok().body(notes);
    }

    @GetMapping(value = "/public/search", produces = "application/json")
    public ResponseEntity<List<NoteResponse>> searchPublicNotes(
            @RequestParam("q")
            @NotBlank
            @Size(max = 50)
            String query
    ) {
        List<NoteResponse> notes = noteService.searchPublicNotes(query).stream().map(NoteModel::toDto).toList();
        return ResponseEntity.ok().body(notes);
    }

    @GetMapping(value = "/{documentId:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}", produces = "application/json")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable UUID documentId) {
        NoteResponse note = noteService.getNoteById(documentId).toDto();
        return ResponseEntity.ok().body(note);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<List<NoteResponse>> getUserNotes(@CurrentUserId UUID userId) {
        return ResponseEntity.ok().body(noteService.getNotesByUserId(userId).stream().map(NoteModel::toDto).toList());
    }

    @GetMapping(value = "/user/search", produces = "application/json")
    public ResponseEntity<List<NoteResponse>> searchUserNotes(
            @RequestParam("q")
            @NotBlank
            @Size(max = 50)
            String query,
            @CurrentUserId UUID userId
    ) {
        List<NoteResponse> notes = noteService.searchUserNotes(query, userId).stream().map(NoteModel::toDto).toList();
        return ResponseEntity.ok(notes);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest noteRequest, @CurrentUserId UUID userId) {
        NoteResponse note = noteService.createNote(noteRequest, userId.toString()).toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(note);

    }

    @DeleteMapping(value = "/{docId}", produces = "application/json")
    public ResponseEntity<Void> deleteNote(
            @PathVariable UUID docId,
            @CurrentUserId UUID userId
    ) {
        noteService.deleteNote(docId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{docId}", consumes = "application/json")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable UUID docId,
            @Valid @RequestBody NoteRequest noteRequest,
            @CurrentUserId UUID userId
    ) {
        NoteResponse updatedNote = noteService.updateNote(docId, noteRequest, userId).toDto();
        return ResponseEntity.ok().body(updatedNote);
    }
}
