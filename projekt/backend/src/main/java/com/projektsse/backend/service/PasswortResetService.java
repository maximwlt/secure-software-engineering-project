package com.projektsse.backend.service;

import com.projektsse.backend.controller.dto.EmailPasswordReset;
import com.projektsse.backend.controller.dto.PasswordResetRequest;
import com.projektsse.backend.exceptions.WrongTokenException;
import com.projektsse.backend.models.PWResetTokenResponse;
import com.projektsse.backend.repository.PWResetRepository;
import com.projektsse.backend.repository.entities.PWResetToken;
import com.projektsse.backend.repository.entities.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class PasswortResetService {

    private final PWResetRepository passwordResetRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final EmailService emailService;

    private final Logger log = LoggerFactory.getLogger(PasswortResetService.class);
    private final PasswordEncoder passwordEncoder;

    public PasswortResetService(PWResetRepository passwordResetRepository, UserService userService, TokenService tokenService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.passwordResetRepository = passwordResetRepository;
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void createPasswordReset(@Valid EmailPasswordReset emailPasswordReset) {
        UUID userId = userService.getUserIdByEmail(emailPasswordReset.email());
        if (userId == null) {
            return; // Keine E-Mail-Benachrichtigung, um Benutzerenumeration zu verhindern
        }

        String resetToken = tokenService.generateOpaqueToken();
        String hashedResetToken = tokenService.hashVerificationToken(resetToken);
        Instant expiryTime = Instant.now().plus(15, MINUTES);
        Instant createTime = Instant.now();

        PWResetToken pwResetToken = new PWResetToken(
                hashedResetToken,
                expiryTime,
                createTime,
                userService.getUserById(userId.toString()) // Was ist wenn userId null ist?
                // Sollte ich alles in einem try catch block abfangen?
        );

        passwordResetRepository.save(pwResetToken);


        String title = "Passwort zurücksetzen";
        String message = String.format("""
                Sie haben eine Anfrage zum Zurücksetzen Ihres Passworts erhalten.
                Klicken Sie auf den folgenden Link, um ein neues Passwort festzulegen:
                http://localhost:8080/api/auth/reset-password?token=%s
                Wenn Sie diese Anfrage nicht gestellt haben, können Sie diese Nachricht ignorieren.
                """, resetToken);

        emailService.sendMail(
                emailPasswordReset.email(),
                title,
                message
        );
    }


    /**
     * Validates the password reset token. If the token is invalid, expired, or already used, a WrongTokenException is thrown.
     * @param token The raw password reset token provided by the user.
     */
    public PWResetTokenResponse validateToken(String token) {
        String hashedToken = tokenService.hashVerificationToken(token);
        Optional<PWResetToken> pwResetTokenOpt = passwordResetRepository.findByTokenHash(hashedToken);
        if (pwResetTokenOpt.isEmpty()) {
            log.warn("Password reset token not found");
            throw new WrongTokenException("Invalid or expired password reset token");
        }

        if (pwResetTokenOpt.get().getExpiresAt().isBefore(Instant.now())) {
            log.warn("Password reset token expired");
            throw new WrongTokenException("Invalid or expired password reset token");
        }

        if (pwResetTokenOpt.get().getUsedAt() != null) {
            log.warn("Password reset token already used");
            throw new WrongTokenException("Invalid or expired password reset token");
        }
        log.info("Password reset token is valid");
        return pwResetTokenOpt.get().toModel();
    }


    /**
     * Invalidates the password reset token by setting its usedAt timestamp to the current time. This prevents the token from being reused.
     * @param pwResetTokenResponse The response model containing the password reset token information, including the associated user.
     */
    private void invalidateToken(PWResetTokenResponse pwResetTokenResponse) {
        PWResetToken tokenEntity = passwordResetRepository.findByTokenHash(
                        pwResetTokenResponse.token())
                .orElseThrow(() -> new WrongTokenException("Invalid or expired password reset token"));
        tokenEntity.setUsedAt(Instant.now());
        passwordResetRepository.save(tokenEntity);
    }



    /**
     * Verifies the password reset request by validating the token, updating the user's password, invalidating all existing refresh tokens for the user, and sending a confirmation email.
     * @param passwordResetReq The password reset request containing the new password and the raw token provided by the user.
     */
    public void verifyPasswordReset(@Valid PasswordResetRequest passwordResetReq) {
        log.info("Verifying password reset request for token: {}", passwordResetReq.token());
        PWResetTokenResponse pwResetTokenResponse = validateToken(passwordResetReq.token());
        log.info("Password reset token validated successfully for user: {}", pwResetTokenResponse.user().getEmail());
        invalidateToken(pwResetTokenResponse);
        log.info("Password reset token invalidated for user: {}", pwResetTokenResponse.user().getEmail());

        String newPasswordHash = passwordEncoder.encode(passwordResetReq.newPassword());
        User user = pwResetTokenResponse.user();
        user.setPassword(newPasswordHash);
        userService.saveUser(user);
        log.info("Password updated successfully for user: {}", user.getEmail());

        log.info("Invalidating all existing refresh tokens for user: {}", user.getEmail());
        tokenService.deleteAllRefreshTokensForUser(user.getId()); // Invalidate all existing refresh tokens

        log.info("Sending password reset confirmation email to user: {}", user.getEmail());
        emailService.sendMail(
                user.getEmail(),
                "Passwort erfolgreich zurückgesetzt",
                "Ihr Passwort wurde erfolgreich zurückgesetzt. Wenn Sie diese Änderung nicht vorgenommen haben, kontaktieren Sie bitte umgehend unseren Support."
        );
    }
}
