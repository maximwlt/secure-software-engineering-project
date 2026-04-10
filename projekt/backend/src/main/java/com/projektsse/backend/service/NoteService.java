package com.projektsse.backend.service;

import com.projektsse.backend.config.MdSanitizer;
import com.projektsse.backend.controller.dto.NoteRequest;
import com.projektsse.backend.exceptions.NoteNotFoundException;
import com.projektsse.backend.exceptions.OpaEvaluationException;
import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.models.opa.OpaInput;
import com.projektsse.backend.models.opa.OpaResource;
import com.projektsse.backend.models.opa.OpaUser;
import com.projektsse.backend.repository.NoteRepository;
import com.projektsse.backend.repository.entities.Note;
import io.github.open_policy_agent.opa.OPAClient;
import io.github.open_policy_agent.opa.OPAException;
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

    private final OPAClient opaClient;

    public NoteService(NoteRepository noteRepository, UserService userService, MdSanitizer mdSanitizer) {
        this.noteRepository = noteRepository;
        this.userService = userService;
        this.mdSanitizer = mdSanitizer;
        this.opaClient = new OPAClient("http://opa:8181");
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

    public void deleteNote(UUID documentId, UUID userId) {
        Note note = noteRepository.findById(documentId)
                .orElseThrow(() -> new NoteNotFoundException(String.format("Note with ID %s not found", documentId)));

        OpaInput input = new OpaInput(
                new OpaUser(userId.toString()),
                new OpaResource(documentId.toString(), "note", note.getOwner().getId().toString()),
                "delete"
        );
        try {
            if (!opaClient.check("notes/allow", input)) {
                throw new NoteNotFoundException(String.format("Note with ID %s not found", documentId));
            }
        } catch (OPAException e) {
            throw new OpaEvaluationException(e);
        }
        noteRepository.delete(note);
    }


    public NoteModel updateNote(UUID docId, @Valid NoteRequest noteRequest, UUID userId) {

        Note noteEntity = noteRepository.findById(docId)
                .orElseThrow(() -> new NoteNotFoundException(String.format("Note with ID %s not found", docId)));

        if (!noteEntity.getOwner().getId().equals(userId)) {
            // Note exists, but belongs to another user, so we throw the same exception to prevent user enumeration
            throw new NoteNotFoundException(String.format("Note with ID %s not found", docId));
        }

        noteEntity.setTitle(mdSanitizer.sanitizeTitle(noteRequest.title()))
            .setMdContent(mdSanitizer.sanitizeContent(noteRequest.mdContent()))
            .setIsPrivate(noteRequest.isPrivate());

        return noteRepository.save(noteEntity).toModel();
    }
}
