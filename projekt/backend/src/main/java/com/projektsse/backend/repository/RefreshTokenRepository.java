package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("SELECT rt.user.id FROM RefreshToken rt WHERE rt.token = :token")
    UUID getUserIdByToken(@Param("token") String token);

    Optional<RefreshToken> findRefreshTokenByTokenAndExpiresAtAfter(String token, Instant expiresAtAfter);

    void deleteAllByUserId(UUID uuid);
}
