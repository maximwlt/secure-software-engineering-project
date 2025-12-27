package com.projektsse.backend.service;

import com.projektsse.backend.controller.dto.NoteReq;
import com.projektsse.backend.exceptions.NoteNotFoundException;
import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.repository.NoteRepository;
import com.projektsse.backend.repository.entities.Note;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
public class NoteService {

    private final UserService userService;
    NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public List<NoteModel> getAllPublicNotes() {
        List<NoteModel> notes = noteRepository.findAllByIsPrivateFalse()
                .stream()
                .map(note -> ((Note) note).toModel())
                .toList();
        return notes;
    }


    public NoteModel createNote(@Valid NoteReq noteReq, String userId) {
        Note note = new Note();
        note.setTitle(noteReq.title());
        note.setMdContent(noteReq.mdContent());
        note.setIsPrivate(noteReq.isPrivate());
        note.setOwner(userService.getUserById(userId));
        Note savedNote = noteRepository.save(note);
        return savedNote.toModel();

    }

    public List<NoteModel> searchPublicNotes(String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<NoteModel> notes = noteRepository.searchPublicNotes(lowerCaseQuery)
                .stream().map(note -> ((Note) note).toModel()).toList();
        return notes;
    }

    public NoteModel getNoteById(UUID documentId) {
        Note note = noteRepository.findById(documentId)
                .orElseThrow(() -> new NoteNotFoundException("Notiz mit der ID " + documentId + " wurde nicht gefunden"));
        return note.toModel();
    }

    public List<NoteModel> getNotesByUserId(UUID userId) {
        List<NoteModel> notes = noteRepository.getNotesByUser_IdOrderByIsPrivateDesc(userId)
                                      .stream().map(Note::toModel).toList();
        return notes;
    }

    public List<NoteModel> searchUserNotes(@NotBlank @Size(max = 50) @Pattern(regexp = "^[a-zA-Z0-9 äöüÄÖÜß!?.,-]*$", message = "Query contains invalid characters") String query, UUID userId) {
        String lowerCaseQuery = query.toLowerCase();
        List<Note> notesEntities = noteRepository.searchUserNotes(userId, lowerCaseQuery);
        List<NoteModel> notes = notesEntities.stream().map(Note::toModel).toList();
        return notes;
    }
}
