package com.projektsse.backend.service;

import com.projektsse.backend.exceptions.VerificationFailedException;
import com.projektsse.backend.exceptions.UserNotFoundException;
import com.projektsse.backend.models.ApiMessageModel;
import com.projektsse.backend.models.UserModel;
import com.projektsse.backend.models.UserReqModel;
import com.projektsse.backend.repository.RegistrationRepository;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.Registration_Request;
import com.projektsse.backend.repository.entities.User;
import jakarta.validation.constraints.NotBlank;
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
    private final RegistrationRepository registrationRepository;

    private static final String DUMMY_HASH = "$argon2id$v=19$m=12288,t=3,p=1$bKJ65XHVvriCKaOG3i3WHw$Ruu7bnU7+IKhuSOAcYungNnDYbzILF5HEperRn5b28Q";

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       EmailService emailService,
                       RegistrationRepository registrationRepository)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.registrationRepository = registrationRepository;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Registration_Request createPendingUser(UserReqModel userReqModel, String verificationCode) {
        return new Registration_Request(
                userReqModel.email(),
                passwordEncoder.encode(userReqModel.password()),
                tokenService.hashVerificationToken(verificationCode),
                Instant.now().plus(3, HOURS)
        );
    }

    public void registerUser(UserReqModel userReqModel) {
        String verificationCode = tokenService.generateOpaqueToken();
        Registration_Request user = createPendingUser(userReqModel, verificationCode);
        registrationRepository.save(user);

        // Sending mail
        String title = "E-Mail verification";
        String message = String.format("""
        Please verify your email address by clicking the following link:
        http://localhost:8080/api/auth/verify-email?code=%s
        """, verificationCode);

        if (existsByEmail(userReqModel.email())) {
            title = "Suspicious registration attempt";
            message = "An attempt was made to register an account with this email address, but it is already in use. If this was not you, please ignore this email.";
        }
        emailService.sendMail(user.getEmail(), title, message);
    }

    public ApiMessageModel verifyUserEmail(String code) {
        Optional<Registration_Request> regReq = registrationRepository
                .findByVerificationCodeAndVerificationCodeExpiryAfter(
                        tokenService.hashVerificationToken(code),
                        Instant.now()
                );

        if (regReq.isEmpty()) {
            throw new VerificationFailedException("Invalid or expired link."); // Kein Benutzer mit diesem Code gefunden
        }
        if (userRepository.existsByEmail(regReq.get().getEmail())) {
            throw new VerificationFailedException("Invalid or expired link."); // E-Mail ist bereits registriert
        }

        User user = new User(
                regReq.get().getEmail(),
                regReq.get().getPassword_hash()
        );
        userRepository.save(user);
        registrationRepository.delete(regReq.get());

        return new ApiMessageModel("Email successfully verified.");
    }

    public UUID getUserIdByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getId).orElse(null);
    }

    public void authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            passwordEncoder.matches(password, DUMMY_HASH); // Dummy-Hash to prevent timing attacks
            throw new IllegalArgumentException("Invalid credentials.");
        }
        User user = userOpt.get();
        String hashedPassword = user.getPassword_hash();
        if (!passwordEncoder.matches(password, hashedPassword)) {
            throw new IllegalArgumentException("Invalid credentials.");
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

    public void deleteUserAccount(UUID userId, @NotBlank(message = "Password cannot be empty") String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        authenticateUser(user.getEmail(), password);
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public UserModel getUserProfile(UUID userId) {
        Optional<UserModel> user = userRepository.findById(userId).map(User::toModel);
        return user.orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
