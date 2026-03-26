package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.*;
import com.projektsse.backend.interfaces.CurrentUserId;
import com.projektsse.backend.models.UserReqModel;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.PasswortResetService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswortResetService passwortResetService;

    public AuthController(UserService userService, JwtService jwtService, TokenService tokenService, PasswortResetService passwortResetService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.passwortResetService = passwortResetService;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiMessage> register(@Validated @RequestBody RegisterReq req) {
        UserReqModel userReqModel = new UserReqModel(req.email(), req.password());
        userService.registerUser(userReqModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiMessage("User successfully registered. Please check your email for verification."));
    }

    @GetMapping(value = "/verify-email", produces = "application/json")
    public ResponseEntity<?> verifyEmail(
            @RequestParam("code")
            @NotBlank(message = "Invalid verification code.")
            @Pattern(regexp = "^[A-Za-z0-9_-]{43}$", message = "Invalid verification code.")
            String code
    ) {
        boolean isVerified = userService.verifyUserEmail(code);
        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "E-Mail erfolgreich verifiziert."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Ungültiger oder abgelaufener Verifizierungslink."));
        }
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest) {

        userService.authenticateUser(loginRequest.email(), loginRequest.password());

        String userId = userService.getUserIdByEmail(loginRequest.email()).toString();

        // Fingerprint generieren
        String fingerprint = jwtService.generateFingerprint();
        String fingerprintHash = jwtService.hashFingerprint(fingerprint);

        // Token mit Fingerprint-Hash erstellen
        String accessToken = jwtService.generateAccessToken(userId, fingerprintHash);
        String refreshToken = tokenService.createRefreshToken(userId);

        Duration durationDays = Duration.ofDays(7);
        Duration accessTokenDuration = Duration.ofMinutes(jwtService.getAccessTokenExpiration()); // Gleich wie JWT Expiry

        // Fingerprint Cookie (HttpOnly, Secure, SameSite=Strict)
        ResponseCookie fingerprintCookie = ResponseCookie.from("__Secure-Fgp", fingerprint)
                                                         .httpOnly(true)
                                                         .secure(cookieSecure)
                                                         .path("/")
                                                         .maxAge(accessTokenDuration) // Max-Age <= JWT Expiry
                                                         .sameSite("Strict")
                                                         .build();

        // Refresh Token Cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                                                          .httpOnly(true)
                                                          .secure(cookieSecure)
                                                          .path("/api/auth/rt")
                                                          .maxAge(durationDays)
                                                          .sameSite("Strict")
                                                          .build();

        return ResponseEntity.ok()
                             .header("Set-Cookie", fingerprintCookie.toString())
                             .header("Set-Cookie", refreshTokenCookie.toString())
                             .body(new AuthResponse(accessToken));
    }

    @PostMapping(value = "/rt/refresh-token", produces = "application/json")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "REFRESH_TOKEN") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kein Refresh-Token Cookie vorhanden."));
        }

        Optional<String> userIdOpt = tokenService.validateRefreshToken(refreshToken);

        if (userIdOpt.isEmpty()) {
            ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                                                        .httpOnly(true).secure(cookieSecure).path("/api/auth/rt")
                                                        .maxAge(0).sameSite("Strict").build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .header("Set-Cookie", deleteCookie.toString())
                                 .body(Map.of("message", "Refresh-Token ungültig oder abgelaufen."));
        }

        String userId = userIdOpt.get();

        // Neuen Fingerprint generieren
        String newFingerprint = jwtService.generateFingerprint();
        String newFingerprintHash = jwtService.hashFingerprint(newFingerprint);

        String newAccessToken = jwtService.generateAccessToken(userId, newFingerprintHash);
        String newRefreshToken = tokenService.rotateRefreshToken(refreshToken);

        Duration accessTokenDuration = Duration.ofMinutes(10);

        ResponseCookie fingerprintCookie = ResponseCookie.from("__Secure-Fgp", newFingerprint)
                                                         .httpOnly(true).secure(cookieSecure).path("/")
                                                         .maxAge(accessTokenDuration).sameSite("Strict").build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
                                                          .httpOnly(true).secure(cookieSecure).path("/api/auth/rt")
                                                          .maxAge(Duration.ofDays(7)).sameSite("Strict").build();

        return ResponseEntity.ok()
                             .header("Set-Cookie", fingerprintCookie.toString())
                             .header("Set-Cookie", refreshTokenCookie.toString())
                             .body(new AuthResponse(newAccessToken));
    }


    @PostMapping(value = "/rt/logout", produces = "application/json")
    public ResponseEntity<ApiMessage> logout(
            @CookieValue(name ="REFRESH_TOKEN", required = false) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.deleteRefreshToken(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth/rt")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie deleteFingerprintCookie = ResponseCookie.from("__Secure-Fgp", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return  ResponseEntity.ok()
                .header("Set-Cookie", deleteCookie.toString())
                .header("Set-Cookie", deleteFingerprintCookie.toString())
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


