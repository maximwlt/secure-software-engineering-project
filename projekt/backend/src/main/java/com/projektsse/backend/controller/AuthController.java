package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.AuthResponse;
import com.projektsse.backend.controller.dto.LoginReq;
import com.projektsse.backend.controller.dto.RegisterReq;
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

        if (userService.existsByEmail(req.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "E-Mail ist bereits registriert")
            );
        }
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
    public ResponseEntity<?> login (@Valid @RequestBody LoginReq loginReq) {

        userService.authenticateUser(loginReq.email(), loginReq.password());

        String userId = userService.getUserIdByEmail(loginReq.email()).toString();
        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = tokenService.createRefreshToken(userId);

        Duration durationDays = Duration.ofDays(7); // CSRF-Token und Refresh-Token Gültigkeit

        // HTTP-only Cookie für Refresh Token setzen
        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                "REFRESH_TOKEN", refreshToken
        ).httpOnly(true)
          .secure(false) // TODO: auf true setzen, wenn HTTPS verwendet wird
          .path("/api/auth/refresh-token")
          .maxAge(durationDays) // 7 Tage
          .sameSite("Strict")
          .build();


        return ResponseEntity.ok()
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body(new AuthResponse(accessToken));
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "REFRESH_TOKEN") String refreshToken
    ) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kein Refresh-Token Cookie vorhanden."));
        }

        Optional<String> userIdOpt = tokenService.validateRefreshToken(refreshToken);

        if (userIdOpt.isEmpty()) {
            ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                                                        .httpOnly(true)
                                                        .secure(false) // TODO: true bei HTTPS
                                                        .path("/api/auth/refresh-token")
                                                        .maxAge(0)
                                                        .sameSite("Strict")
                                                        .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .header("Set-Cookie", deleteCookie.toString())
                                 .body(Map.of("message", "Refresh-Token ungültig oder abgelaufen."));
        }
        String userId = userIdOpt.get();

        String newAccessToken = jwtService.generateAccessToken(userId);
        String newRefreshToken = tokenService.rotateRefreshToken(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                "REFRESH_TOKEN", newRefreshToken
        ).httpOnly(true)
          .secure(false) // TODO: auf true setzen, wenn HTTPS verwendet wird
          .path("/api/auth/refresh-token")
          .maxAge(Duration.ofDays(7)) // 7 Tage
          .sameSite("Strict")
          .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body(new AuthResponse(newAccessToken));

        // CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // request.setAttribute("NEW_CSRF_TOKEN", csrfToken.getToken());
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name ="REFRESH_TOKEN", required = false) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.deleteRefreshToken(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false) // TODO: auf true setzen, wenn HTTPS verwendet wird
                .path("/api/auth/refresh-token")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return  ResponseEntity.ok()
                .header("Set-Cookie", deleteCookie.toString())
                .body(Map.of("message", "Erfolgreich abgemeldet."));
    }
}


