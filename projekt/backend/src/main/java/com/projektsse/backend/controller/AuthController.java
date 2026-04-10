package com.projektsse.backend.controller;

import com.projektsse.backend.controller.components.AuthCookieFactory;
import com.projektsse.backend.controller.dto.*;
import com.projektsse.backend.interfaces.CurrentUserId;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.PasswortResetService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswortResetService passwortResetService;
    private final AuthCookieFactory authCookieFactory;

    public AuthController(UserService userService, JwtService jwtService, TokenService tokenService, PasswortResetService passwortResetService, AuthCookieFactory authCookieFactory) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.passwortResetService = passwortResetService;
        this. authCookieFactory = authCookieFactory;
    }



    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiMessage> register(@Validated @RequestBody RegisterRequest req) {
        userService.registerUser(req.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiMessage("User successfully registered. Please check your email for verification."));
    }

    @GetMapping(value = "/verify-email", produces = "application/json")
    public ResponseEntity<ApiMessage> verifyEmail(
            @RequestParam("code")
            @NotBlank(message = "Cannot be blank.")
            @Pattern(regexp = "^[A-Za-z0-9_-]{43}$", message = "Invalid or expired link.")
            String code
    ) {
        return ResponseEntity.ok(userService.verifyUserEmail(code).toDto());
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest loginRequest) {

        userService.authenticateUser(loginRequest.email(), loginRequest.password());

        String userId = userService.getUserIdByEmail(loginRequest.email()).toString();

        // Fingerprint generieren
        String fingerprint = jwtService.generateFingerprint();
        String fingerprintHash = jwtService.hashFingerprint(fingerprint);

        // Token mit Fingerprint-Hash erstellen
        String accessToken = jwtService.generateAccessToken(userId, fingerprintHash);
        String refreshToken = tokenService.createRefreshToken(userId);

        Duration accessTokenDuration = Duration.ofMinutes(jwtService.getAccessTokenExpiration()); // Gleich wie JWT Expiry

        return ResponseEntity.ok()
                             .header("Set-Cookie", authCookieFactory.setRefreshTokenCookie(refreshToken).toString())
                             .header("Set-Cookie", authCookieFactory.setFingerprintCookie(fingerprint, accessTokenDuration).toString())
                             .body(new AuthResponse(accessToken));
    }

    @PostMapping(value = "/rt/refresh-token", produces = "application/json")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "REFRESH_TOKEN") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Refresh-Token is missing."));
        }

        Optional<String> userIdOpt = tokenService.validateRefreshToken(refreshToken);

        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .header("Set-Cookie", authCookieFactory.deleteRefreshTokenCookie().toString())
                                 .body(new AuthResponse("Invalid or expired refresh token."));
        }

        String userId = userIdOpt.get();

        // Neuen Fingerprint generieren
        String newFingerprint = jwtService.generateFingerprint();
        String newFingerprintHash = jwtService.hashFingerprint(newFingerprint);

        String newAccessToken = jwtService.generateAccessToken(userId, newFingerprintHash);
        String newRefreshToken = tokenService.rotateRefreshToken(refreshToken);

        Duration accessTokenDuration = Duration.ofMinutes(jwtService.getAccessTokenExpiration());

        return ResponseEntity.ok()
                             .header("Set-Cookie", authCookieFactory.setFingerprintCookie(newFingerprint, accessTokenDuration).toString())
                             .header("Set-Cookie", authCookieFactory.setRefreshTokenCookie(newRefreshToken).toString())
                             .body(new AuthResponse(newAccessToken));
    }


    @PostMapping(value = "/rt/logout", produces = "application/json")
    public ResponseEntity<ApiMessage> logout(
            @CookieValue(name ="REFRESH_TOKEN", required = false) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.deleteRefreshToken(refreshToken);
        }

        return  ResponseEntity.ok()
                .header("Set-Cookie", authCookieFactory.deleteRefreshTokenCookie().toString())
                .header("Set-Cookie", authCookieFactory.deleteFingerprintCookie().toString())
                .body(new ApiMessage("Successfully logged out."));
    }


    @DeleteMapping(value = "/me", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiMessage> deleteAccount(
            @CurrentUserId UUID userId,
            @Valid @RequestBody DeleteAccountReq deleteAccountReq
    ) {
        userService.deleteUserAccount(userId, deleteAccountReq.password());
        return ResponseEntity.ok(new ApiMessage("Account successfully deleted."));
    }


    @PostMapping(value = "/forgot-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiMessage> forgotPassword(@Validated @RequestBody EmailPasswordReset emailPasswordReset) {
        passwortResetService.createPasswordReset(emailPasswordReset);
        return ResponseEntity.ok(new ApiMessage("If an account with that email exists, a password reset link has been sent."));
    }

    @GetMapping(value = "/reset-password", produces = "application/json")
    public ResponseEntity<ApiMessage> handleResetLink(@RequestParam String token) {
        passwortResetService.validateToken(token);
        return ResponseEntity.ok(new ApiMessage("Token is valid. You can proceed to reset your password."));
    }

    @PostMapping(value = "/reset-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiMessage> verifyPasswordReset(@Validated @RequestBody PasswordResetRequest passwordResetReq) {
        passwortResetService.verifyPasswordReset(passwordResetReq);
        return ResponseEntity.ok(new ApiMessage("Password reset successful."));
    }

    @GetMapping(value = "/csrf")
    public ResponseEntity<Void> getCsrfToken() {
        return ResponseEntity.ok().build();
    }

}


