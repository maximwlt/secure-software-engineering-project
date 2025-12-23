package com.projektsse.backend.repository;

import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.repository.entities.Note;
import com.projektsse.backend.repository.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface NoteRepository extends CrudRepository<Note, UUID> {
    Collection<Object> findAllByIs_privateIsFalse();

    UUID user(User user);

    List<Note> getNotesByUser_IdOrderByIs_privateDesc(UUID userId);

    List<Note> findAllByIsPrivateFalseAndTitleContainingIgnoreCaseOrIsPrivateFalseAndMdContentContainingIgnoreCase(
            String titleQuery, String contentQuery);

    List<Note> getNotesByUser_IdAndTitleContainingIgnoreCaseOrMd_contentIsContainingIgnoreCase(UUID userId, String lowerCaseQuery, String lowerCaseQuery1);
}
