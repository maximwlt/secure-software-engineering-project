package com.projektsse.backend.repository.entities;

import com.projektsse.backend.models.NoteModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @CreationTimestamp
    @Column(name = "created_at")
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

    public void setTitle(@NotBlank(message = "Titel darf nicht leer sein") @Size(max = 255, message = "Titel darf maximal 255 Zeichen lang sein") String title) {
        this.title = title;
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
        note.setNoteId(model.noteId());
        note.setTitle(model.title());
        note.setMdContent(model.md_content());
        note.setIsPrivate(model.is_private());
        note.setOwner(owner);
        return note;
    }



    public void setMdContent(@NotBlank(message = "Inhalt darf nicht leer sein") @Size(max = 10000, message = "Inhalt darf maximal 10.000 Zeichen lang sein") String mdContent) {
        this.mdContent = mdContent;
    }

    public void setIsPrivate(@NotNull(message = "Sichtbarkeit muss angegeben werden") boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public User getOwner() {
        return this.user;
    }
}
