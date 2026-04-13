package com.projektsse.backend.repository.entities;

import com.projektsse.backend.repository.enums.TeamRole;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    public TeamMember() {}
}