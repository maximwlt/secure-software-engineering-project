package com.projektsse.backend.service;

import com.projektsse.backend.exceptions.UserNotFoundException;
import com.projektsse.backend.models.UserReqModel;
import com.projektsse.backend.repository.RegistrationRepository;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.Registration_Request;
import com.projektsse.backend.repository.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final RefreshTokenHasher verificationTokenHasher;
    private final RegistrationRepository registrationRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       EmailService emailService,
                       RefreshTokenHasher verificationTokenHasher,
                       RegistrationRepository registrationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.verificationTokenHasher = verificationTokenHasher;
        this.registrationRepository = registrationRepository;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Registration_Request createPendingUser(UserReqModel userReqModel, String verificationCode) {
        return new Registration_Request(
                userReqModel.email(),
                passwordEncoder.encode(userReqModel.password()),
                verificationTokenHasher.hash(verificationCode),
                Instant.now().plus(3, HOURS)
        );
    }

    public void registerUser(UserReqModel userReqModel) {
        String verificationCode = tokenService.generateOpaqueToken();
        Registration_Request user = createPendingUser(userReqModel, verificationCode);
        registrationRepository.save(user);

        // E-Mail versenden
        String title = "E-Mail Verifizierung";
        String message = String.format("""
        Bitte verifizieren Sie Ihre E-Mail-Adresse, indem Sie auf den folgenden Link klicken:
        http://localhost:8080/api/auth/verify-email?code=%s
        """, verificationCode);
        // TODO: Domain anpassen, wenn HTTPS und Reverse Proxy eingerichtet wurden


        if (existsByEmail(userReqModel.email())) {
            title = "Hinweis auf ungewöhnliche Registrierungsaktivität";
            message = "Es wurde versucht, diese E-Mail-Adresse erneut zu registrieren. " +
                    "Wenn Sie das nicht waren, können Sie diese Nachricht ignorieren.";
        }

        emailService.sendVerificationEmail(
                user.getEmail(),
                title,
                message
        );

    }

    public boolean verifyUserEmail(@NotBlank(message = "Ungültiger Verifizierungscode.") @Size(min = 43, max = 43, message = "Ungültiger Verifizierungscode.") String code) {

        //Optional<User> user = userRepository.findByVerificationCode(code);
        Optional<Registration_Request> regReq = registrationRepository
                .findByVerificationCodeAndVerificationCodeExpiryAfter(
                        verificationTokenHasher.hash(code),
                        Instant.now()
                );

        if (regReq.isEmpty()) {
            return false; // Kein Benutzer mit diesem Code gefunden
        }
        if (userRepository.existsByEmail(regReq.get().getEmail())) {
            return false; // E-Mail ist bereits registriert
        }

        User user = new User(
                regReq.get().getEmail(),
                regReq.get().getPassword_hash()
        );
        userRepository.save(user);
        registrationRepository.delete(regReq.get());

        return true; // Erfolgreich verifiziert

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
        String hashedPassword = user.getPassword_hash();
        if (!passwordEncoder.matches(password, hashedPassword)) {
            throw new IllegalArgumentException("Ungültige Anmeldedaten.");
        }
        // Wenn Konfiguration von Argon2id geändert wurde, soll gerehashed werden
        if (passwordEncoder instanceof Argon2PasswordEncoder && passwordEncoder.upgradeEncoding(user.getPassword_hash())) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
    }


    public User getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId)).orElse(null);
    }

    public void deleteUserAccount(UUID userId, @NotBlank(message = "Passwort darf nicht leer sein.") String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Benutzer nicht gefunden."));
        authenticateUser(user.getEmail(), password);
        userRepository.delete(user);
    }
}
