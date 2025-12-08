package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NoteRepository extends CrudRepository<Note, UUID> {
}
