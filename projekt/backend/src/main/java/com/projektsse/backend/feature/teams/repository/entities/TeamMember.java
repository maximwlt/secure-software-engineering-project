package com.projektsse.backend.feature.teams.repository.entities;

import com.projektsse.backend.feature.auth.repository.entities.User;
import com.projektsse.backend.feature.teams.model.enums.TeamRole;
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