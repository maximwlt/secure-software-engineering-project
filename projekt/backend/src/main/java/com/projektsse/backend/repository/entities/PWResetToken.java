package com.projektsse.backend.repository.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pw_reset_tokens")
public class PWResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private String token;

    private LocalDateTime expires_at;

    private boolean used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
