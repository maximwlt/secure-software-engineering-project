package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.PWResetToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PWResetRepository extends CrudRepository<PWResetToken, UUID> {
    Optional<PWResetToken> findByTokenHash(String tokenHash);
}
