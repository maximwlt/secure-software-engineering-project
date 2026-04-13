package com.projektsse.backend.feature.notes.service;

import com.projektsse.backend.feature.notes.components.MdSanitizer;
import com.projektsse.backend.feature.notes.api.dto.NoteRequest;
import com.projektsse.backend.shared.exceptions.NoteNotFoundException;
import com.projektsse.backend.feature.notes.model.NoteModel;
import com.projektsse.backend.shared.models.opa.OpaInput;
import com.projektsse.backend.shared.models.opa.OpaResource;
import com.projektsse.backend.shared.models.opa.OpaUser;
import com.projektsse.backend.feature.notes.repository.NoteRepository;
import com.projektsse.backend.feature.notes.repository.entities.Note;
import com.projektsse.backend.shared.service.OpaService;
import com.projektsse.backend.feature.auth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private final UserService userService;
    private final NoteRepository noteRepository;
    private final MdSanitizer mdSanitizer;
    private final OpaService opaService;

    public NoteService(NoteRepository noteRepository, UserService userService, MdSanitizer mdSanitizer, OpaService opaService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
        this.mdSanitizer = mdSanitizer;
        this.opaService = opaService;
    }

    public List<NoteModel> getAllPublicNotes() {
        return noteRepository.findAllByIsPrivateFalse()
                .stream()
                .map(note -> ((Note) note).toModel())
                .toList();
    }


    public NoteModel createNote(@Valid NoteRequest noteRequest, String userId) {
        Note note = new Note();
        note.setTitle(mdSanitizer.sanitizeTitle(noteRequest.title()));
        note.setMdContent(mdSanitizer.sanitizeContent(noteRequest.mdContent()));
        note.setIsPrivate(noteRequest.isPrivate());
        note.setOwner(userService.getUserById(userId));
        Note savedNote = noteRepository.save(note);
        return savedNote.toModel();

    }

    public List<NoteModel> searchPublicNotes(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();
        return noteRepository.searchPublicNotes(lowerCaseQuery)
                .stream().map(Note::toModel).toList();
    }

    public NoteModel getNoteById(UUID documentId) {
        Note note = noteRepository.findById(documentId)
                .orElseThrow(() -> new NoteNotFoundException(String.format("Note with ID %s not found", documentId)));
        return note.toModel();
    }

    public List<NoteModel> getNotesByUserId(UUID userId) {
        return noteRepository.getNotesByUser_IdOrderByIsPrivateDesc(userId)
                                      .stream().map(Note::toModel).toList();
    }

    public List<NoteModel> searchUserNotes(@NotBlank @Size(max = 50) @Pattern(regexp = "^[a-zA-Z0-9 äöüÄÖÜß!?.,-]*$", message = "Query contains invalid characters") String query, UUID userId) {
        String lowerCaseQuery = query.toLowerCase();
        List<Note> notesEntities = noteRepository.searchUserNotes(userId, lowerCaseQuery);
        return notesEntities.stream().map(Note::toModel).toList();
    }

    public void deleteNote(UUID documentId, UUID userId) {
        Note note = noteRepository.findById(documentId)
                .orElseThrow(() -> new NoteNotFoundException(String.format("Note with ID %s not found", documentId)));

        OpaInput input = new OpaInput(
                new OpaUser(userId.toString()),
                new OpaResource(documentId.toString(), "note", note.getOwner().getId().toString()),
                "delete"
        );

        if (opaService.check("notes/allow", input)) {
            noteRepository.delete(note);
        } else {
            throw new NoteNotFoundException(String.format("Note with ID %s not found", documentId));
        }
    }


    public NoteModel updateNote(UUID docId, @Valid NoteRequest noteRequest, UUID userId) {
        Note noteEntity = noteRepository.findById(docId)
                .orElseThrow(() -> new NoteNotFoundException(String.format("Note with ID %s not found", docId)));

        OpaInput input = new OpaInput(
                new OpaUser(userId.toString()),
                new OpaResource(docId.toString(), "note", noteEntity.getOwner().getId().toString()),
                "update"
        );
        if (!opaService.check("notes/allow", input)) {
            // Note exists, but belongs to another user, so we throw the same exception to prevent user enumeration
            throw new NoteNotFoundException(String.format("Note with ID %s not found", docId));
        }

        noteEntity.setTitle(mdSanitizer.sanitizeTitle(noteRequest.title()))
            .setMdContent(mdSanitizer.sanitizeContent(noteRequest.mdContent()))
            .setIsPrivate(noteRequest.isPrivate());

        return noteRepository.save(noteEntity).toModel();
    }
}
