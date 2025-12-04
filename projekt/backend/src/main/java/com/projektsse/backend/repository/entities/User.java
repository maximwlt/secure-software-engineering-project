package com.projektsse.backend.repository.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name="email", nullable = false, unique = true, length = 255)
    private String email;
    @Column(name="password_hash", nullable = false, length = 255)
    private String password_hash;
    @CreationTimestamp
    @Column(name="created_at", updatable = false, nullable = false)
    private LocalDateTime created_at;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updated_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void addNote(Note note) {
        notes.add(note);
        note.setOwner(this);
    }

    public void removeNote(Note note) {
        notes.remove(note);
        note.setOwner(null);
    }



    public List<Note> getNotes() {
        return notes;
    }
}


/*
Option 2: Tabelle manuell in der DB erstellen (für Production)
Führe dieses SQL-Statement in deiner Datenbank aus:
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
Best Practice für Projekte:
Entwicklung: ddl-auto: create-drop oder create
Production: ddl-auto: validate + Liquibase/Flyway für Migrations
 */