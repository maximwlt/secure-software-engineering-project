package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.json.JsonComparator;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.junit.jupiter.api.Assertions.*;

@ControllerAdvice
class AuthControllerTest {

    private RestTestClient client;
    private UserService userService;
    private JwtService jwtService;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        jwtService = Mockito.mock(JwtService.class);
        tokenService = Mockito.mock(TokenService.class);
        client = RestTestClient.bindToController(new AuthController(userService, jwtService, tokenService)).build();
    }

    @Test
    void register() {
        client.post()
                .uri("/api/auth/register")
                .body(new RegisterReq("frankestein@gmail.com", "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung.");
    }
}