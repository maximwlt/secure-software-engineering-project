package com.projektsse.backend.controller;

import com.projektsse.backend.controller.dto.LoginReq;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.PasswortResetService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.client.RestTestClient;


class AuthControllerTest {

    private RestTestClient client;
    private UserService userService;
    private JwtService jwtService;
    private TokenService tokenService;
    private PasswortResetService passwortResetService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        jwtService = Mockito.mock(JwtService.class);
        tokenService = Mockito.mock(TokenService.class);
        passwortResetService = Mockito.mock(PasswortResetService.class);
        client = RestTestClient.bindToController(new AuthController(userService, jwtService, tokenService, passwortResetService)).build();
    }

    @Test
    void registerStrongPass() {
        client.post()
                .uri("/api/auth/register")
                .body(new RegisterReq("frankestein@gmail.com", "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung.");
    }

//    @Test
//    void registerWeakPass() {
//        client.post()
//                .uri("/api/auth/register")
//                .body(new RegisterReq("frankestein@gmail.com", "Password123"))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$.message").isEqualTo("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung.");
//    }
}