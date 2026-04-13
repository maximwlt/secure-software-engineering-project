package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
}
