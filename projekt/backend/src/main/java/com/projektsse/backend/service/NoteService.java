package com.projektsse.backend.service;

import com.projektsse.backend.controller.dto.NoteReq;
import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.repository.NoteRepository;
import com.projektsse.backend.repository.entities.Note;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final UserService userService;
    NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public List<NoteModel> getAllPublicNotes() {
        List<NoteModel> notes = noteRepository.findAllByIs_privateIsFalse()
                .stream()
                .map(note -> ((Note) note).toModel())
                .toList();
        return notes;
    }


    public NoteModel createNote(@Valid NoteReq noteReq, String userId) {
        Note note = new Note();
        note.setTitle(noteReq.title());
        note.setMd_content(noteReq.mdContent());
        note.setIs_private(noteReq.isPrivate());
        note.setOwner(userService.getUserById(userId));
        Note savedNote = noteRepository.save(note);
        return savedNote.toModel();

    }
}
