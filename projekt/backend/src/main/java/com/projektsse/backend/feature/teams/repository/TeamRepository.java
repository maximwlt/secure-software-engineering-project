package com.projektsse.backend.feature.teams.repository;

import com.projektsse.backend.feature.teams.repository.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
}
