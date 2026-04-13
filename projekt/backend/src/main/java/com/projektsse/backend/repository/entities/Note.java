package com.projektsse.backend.repository.entities;

import com.projektsse.backend.models.NoteModel;
import com.projektsse.backend.repository.enums.NoteStatus;
import com.projektsse.backend.repository.enums.NoteVisibility;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "note_id")
    private UUID noteId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "md_content", nullable = false, length = 10000)
    private String mdContent;

    @Column(name = "is_private")
    private boolean isPrivate;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private NoteVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoteStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public void setOwner(User user) {
        this.user = user;
    }

    public NoteModel toModel() {
        return new NoteModel(
            this.noteId,
            this.title,
            this.mdContent,
            this.isPrivate,
            this.createdAt,
            this.updatedAt,
            this.user != null ? this.user.getId() : null
        );
    }

    public static Note fromModel(NoteModel model, User owner) {
        Note note = new Note();
        note.setTitle(model.title());
        note.setMdContent(model.md_content());
        note.setIsPrivate(model.is_private());
        note.setOwner(owner);
        return note;
    }


    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public Note setMdContent(String mdContent) {
        this.mdContent = mdContent;
        return this;
    }

    public Note setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    public User getOwner() {
        return this.user;
    }
}
