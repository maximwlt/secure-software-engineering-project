package com.projektsse.backend.repository;

import com.projektsse.backend.repository.entities.Registration_Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration_Request, Long> {
    Optional<Registration_Request> findByVerificationCodeAndVerificationCodeExpiryAfter(String hash, Instant now);
}
