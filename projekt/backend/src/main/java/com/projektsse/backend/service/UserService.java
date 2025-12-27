package com.projektsse.backend.service;

import com.projektsse.backend.models.UserReqModel;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.User;
import com.projektsse.backend.repository.entities.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createPendingUser(UserReqModel userReqModel, String verificationCode) {
        User user = new User();
        user.setEmail(userReqModel.email());
        String hashedPassword = passwordEncoder.encode(userReqModel.password());
        user.setPassword(hashedPassword);
        user.setStatus(UserStatus.PENDING);
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusHours(3));
        return userRepository.save(user);
    }

    public void registerUser(UserReqModel userReqModel) {
        String verificationCode = tokenService.generateOpaqueToken();
        User user = createPendingUser(userReqModel, verificationCode);
        userRepository.save(user);

        // E-Mail versenden
        String title = "E-Mail Verifizierung";
        String message = String.format("""
        Bitte verifizieren Sie Ihre E-Mail-Adresse, indem Sie auf den folgenden Link klicken:
        http://localhost:8080/api/auth/verify-email?code=%s
        """, verificationCode);

        // TODO: Domain anpassen, wenn HTTPS und Reverse Proxy eingerichtet wurden
        // TODO: GlobalExceptionHandler implementieren (allgemein)
        emailService.sendVerificationEmail(
                user.getEmail(),
                title,
                message
        );

    }

    public boolean verifyUserEmail(@NotBlank(message = "Ungültiger Verifizierungscode.") @Size(min = 43, max = 43, message = "Ungültiger Verifizierungscode.") String code) {
        Optional<User> user = userRepository.findByVerificationCode(code);

        if (user.isEmpty()) {
            return false; // Kein Benutzer mit diesem Code gefunden
        }
        User foundUser = user.get();
        if (foundUser.getStatus() == UserStatus.VERIFIED) {
            return false; // Bereits verifiziert
        }

        if (foundUser.getStatus() == UserStatus.PENDING) {
            if (foundUser.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                return false; // Code ist abgelaufen
            }
            foundUser.setStatus(UserStatus.VERIFIED);
            foundUser.setVerifiedAt(LocalDateTime.now());
            foundUser.setVerificationCode(null); // Code löschen
            foundUser.setVerificationCodeExpiry(null); // Ablaufdatum löschen
            userRepository.save(foundUser);
            return true; // Erfolgreich verifiziert
        } else {
            return false; // Ungültiger Status
        }
    }

    public UUID getUserIdByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getId).orElse(null);
    }

    public void authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Ungültige Anmeldedaten.");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            throw new IllegalArgumentException("Ungültige Anmeldedaten.");
        }
        if (user.getStatus() != UserStatus.VERIFIED) {
            throw new IllegalStateException("Benutzerkonto ist nicht verifiziert.");
        }
    }

    public User getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId)).orElse(null);
    }
}
