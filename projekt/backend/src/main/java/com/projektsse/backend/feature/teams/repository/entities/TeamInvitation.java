package com.projektsse.backend.feature.teams.repository.entities;

import com.projektsse.backend.feature.auth.repository.entities.User;
import com.projektsse.backend.feature.teams.model.enums.InvitationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "team_invitations")
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status; // PENDING, ACCEPTED, DECLINED, REVOKED, EXPIRED

    @Column(nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User invitee; // Who was invited?

    @ManyToOne
    @JoinColumn(nullable = false)
    private User invitedBy; // Who sent invite?

    public TeamInvitation() {}
}