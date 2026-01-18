package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.AuthResponse;
import com.projektsse.backend.controller.dto.DeleteAccountReq;
import com.projektsse.backend.controller.dto.LoginReq;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.interfaces.CurrentUserId;
import com.projektsse.backend.models.UserReqModel;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.Pattern;
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

    UserService userService;
    JwtService jwtService;
    TokenService tokenService;

    public AuthController(UserService userService, JwtService jwtService, TokenService tokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req) {

        UserReqModel userReqModel = new UserReqModel(req.email(), req.password());

        userService.registerUser(userReqModel);

        return ResponseEntity.status(HttpStatus.CREATED)
             .body(Map.of(
                "message", "Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung."
            ));

    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @RequestParam("code")
            @NotBlank(message = "Ungültiger Verifizierungscode.")
            @Size(min = 43, max = 43, message = "Ungültiger Verifizierungscode.")
            @Pattern(regexp = "^[A-Za-z0-9_-]{43}$", message = "Ungültiger Verifizierungscode.")
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


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginReq) {

        userService.authenticateUser(loginReq.email(), loginReq.password());

        String userId = userService.getUserIdByEmail(loginReq.email()).toString();

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
                                                         .secure(true)
                                                         .path("/")
                                                         .maxAge(accessTokenDuration) // Max-Age <= JWT Expiry
                                                         .sameSite("Strict")
                                                         .build();

        // Refresh Token Cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                                                          .httpOnly(true)
                                                          .secure(true)
                                                          .path("/api/auth/rt")
                                                          .maxAge(durationDays)
                                                          .sameSite("Strict")
                                                          .build();

        return ResponseEntity.ok()
                             .header("Set-Cookie", fingerprintCookie.toString())
                             .header("Set-Cookie", refreshTokenCookie.toString())
                             .body(new AuthResponse(accessToken));
    }

    @PostMapping("/rt/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "REFRESH_TOKEN") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kein Refresh-Token Cookie vorhanden."));
        }

        Optional<String> userIdOpt = tokenService.validateRefreshToken(refreshToken);

        if (userIdOpt.isEmpty()) {
            ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                                                        .httpOnly(true).secure(true).path("/api/auth/rt")
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
                                                         .httpOnly(true).secure(true).path("/")
                                                         .maxAge(accessTokenDuration).sameSite("Strict").build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
                                                          .httpOnly(true).secure(true).path("/api/auth/rt")
                                                          .maxAge(Duration.ofDays(7)).sameSite("Strict").build();

        return ResponseEntity.ok()
                             .header("Set-Cookie", fingerprintCookie.toString())
                             .header("Set-Cookie", refreshTokenCookie.toString())
                             .body(new AuthResponse(newAccessToken));
    }


    @PostMapping("/rt/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name ="REFRESH_TOKEN", required = false) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.deleteRefreshToken(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/rt")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie deleteFingerprintCookie = ResponseCookie.from("__Secure-Fgp", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return  ResponseEntity.ok()
                .header("Set-Cookie", deleteCookie.toString())
                .header("Set-Cookie", deleteFingerprintCookie.toString())
                .body(Map.of("message", "Erfolgreich abgemeldet."));
    }


    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(
            @CurrentUserId UUID userId,
            @Valid @RequestBody DeleteAccountReq deleteAccountReq
    ) {
        userService.deleteUserAccount(userId, deleteAccountReq.password());

        return  ResponseEntity.ok()
                .body(Map.of("message", "Benutzerkonto erfolgreich gelöscht."));
    }
}


