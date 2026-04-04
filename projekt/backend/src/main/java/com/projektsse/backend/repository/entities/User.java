package com.projektsse.backend.repository.entities;

import com.projektsse.backend.models.UserModel;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PWResetToken> pwResetTokens = new ArrayList<>();

    public User(String email, String passwordHash) {
        this.email = email;
        this.password_hash = passwordHash;
    }

    public UserModel toModel() {
        return new UserModel(
            this.id,
            this.email,
            this.created_at,
            this.updated_at
        );
    }

    public User() {}

    public UUID getId() { return id; }

    public String getEmail() { return email; }

    public void setPassword(String hashedPassword) { this.password_hash = hashedPassword; }
    public String getPassword_hash() {
        return password_hash;
    }
}
