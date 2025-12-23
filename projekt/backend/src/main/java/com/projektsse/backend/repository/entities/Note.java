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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "md_content", nullable = false, length = 1000)
    private String md_content;

    @Column(name = "is_private")
    private boolean is_private;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }
    public String getTitle() {
        return title;
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
            this.md_content,
            this.is_private,
            this.created_at,
            this.updated_at,
            this.user != null ? this.user.getId() : null
        );
    }


    public void setMd_content(@NotBlank(message = "Inhalt darf nicht leer sein") @Size(max = 10000, message = "Inhalt darf maximal 10.000 Zeichen lang sein") String s) {
    }

    public void setIs_private(@NotNull(message = "Sichtbarkeit muss angegeben werden") boolean aPrivate) {
    }
}
