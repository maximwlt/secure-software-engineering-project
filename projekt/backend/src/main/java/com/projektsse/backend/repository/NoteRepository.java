package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.Note;
import com.projektsse.backend.repository.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends CrudRepository<Note, UUID> {

    UUID user(User user);

    List<Note> findAllByIsPrivateFalse();
    List<Note> getNotesByUser_IdOrderByIsPrivateDesc(UUID userId);

    @Query("SELECT n FROM Note n WHERE n.isPrivate = false AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(n.mdContent) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Note> searchPublicNotes(@Param("query") String query);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(n.mdContent) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Note> searchUserNotes(@Param("userId") UUID userId, @Param("query") String query);

}
