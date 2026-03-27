package com.projektsse.backend.controller.Unit.Authentication;

import com.projektsse.backend.controller.AuthController;
import com.projektsse.backend.controller.dto.RegisterRequest;
import com.projektsse.backend.exceptions.GlobalExceptionHandler;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.PasswortResetService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
  References: https://docs.spring.io/spring-framework/reference/testing/resttestclient.html
 */

class AuthControllerTest {

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        UserService userService = Mockito.mock(UserService.class);
        JwtService jwtService = Mockito.mock(JwtService.class);
        TokenService tokenService = Mockito.mock(TokenService.class);
        PasswortResetService passwortResetService = Mockito.mock(PasswortResetService.class);
        client = RestTestClient.bindToController(
                        new AuthController(userService, jwtService, tokenService, passwortResetService),
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void registerStrongPass() {
        client.post()
                .uri("/api/auth/register")
                .body(new RegisterRequest("frankestein@gmail.com", "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung.");
    }

    @Test
    void registerWeakPass() {
        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("frankestein@gmail.com", "Password123"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problem -> {
                    assertNotNull(problem);
                    assertNotNull(problem.getTitle());
                    assertEquals("Password does not meet security requirements", problem.getTitle());
                    assertEquals(400, problem.getStatus());
                    assertEquals(URI.create("/api/auth/register"), problem.getInstance());
                    assertEquals("Password is too weak.", problem.getDetail());
                });
    }

    @Test
    void registerWithEmailNull() {
        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest(null, "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problem -> {
                    assertNotNull(problem);
                    assertNotNull(problem.getTitle());
                    assertNotNull(problem.getProperties());
                    assertEquals("Validation Error", problem.getTitle());
                    assertEquals(400, problem.getStatus());
                    assertEquals(URI.create("/api/auth/register"), problem.getInstance());
                    assertTrue(problem.getProperties().containsKey("errors"));
                    List<Map<String, String>> errors = (List<Map<String, String>>) problem.getProperties().get("errors");
                    assertFalse(errors.isEmpty());
                    assertTrue(errors.stream().anyMatch(error -> "email".equals(error.get("field")) && "Email must not be empty".equals(error.get("message"))));
                });
    }

    @Test
    void registerWithEmailEmpty() {
        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("", "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problem -> {
                    assertNotNull(problem);
                    assertNotNull(problem.getTitle());
                    assertNotNull(problem.getProperties());
                    assertEquals("Validation Error", problem.getTitle());
                    assertEquals(400, problem.getStatus());
                    assertEquals(URI.create("/api/auth/register"), problem.getInstance());
                    assertTrue(problem.getProperties().containsKey("errors"));
                    List<Map<String, String>> errors = (List<Map<String, String>>) problem.getProperties().get("errors");
                    assertFalse(errors.isEmpty());
                    assertTrue(errors.stream().anyMatch(error -> "email".equals(error.get("field")) && "Email must not be empty".equals(error.get("message"))));
                });
    }

    @Test
    void registerEmailTooLong() {

        String longEmail = "a".repeat(247) + "@test.com"; // >255 Zeichen

        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest(longEmail, "Xfr@nke41!g+6&4"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problem -> {
                    assertNotNull(problem);
                    assertNotNull(problem.getProperties());
                    assertEquals("Validation Error", problem.getTitle());
                    assertEquals(400, problem.getStatus());
                    assertEquals(URI.create("/api/auth/register"), problem.getInstance());
                    assertTrue(problem.getProperties().containsKey("errors"));
                    List<Map<String, String>> errors = (List<Map<String, String>>) problem.getProperties().get("errors");
                    assertFalse(errors.isEmpty());
                    assertTrue(errors.stream().anyMatch(error -> "email".equals(error.get("field")) && "Email must be at most 255 characters long".equals(error.get("message"))));
                });
    }


    @Test
    void passwordContainsEmailCredential() {
        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("maxmustermann@gmail.com", "MaxMustermann!Sec!"))
                .exchange()
                .expectStatus().isBadRequest();

        client.post()
                .uri("/api/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("seline.schaefer@gmail.com", "MaxMustermann!Sec!"))
                .exchange()
                .expectStatus().isCreated();
    }


    /*@Test
    void passwordResetWithEmptyPassword() {
        pwResetRepository.save(new PWResetToken(// add data));
        client.post()
                .uri("api/auth/reset-password")
                .accept(MediaType.APPLICATION_JSON)
                .body(new PasswordResetRequest("qLj573cvV6iP71TUxa16xGv0PxTkcH-FqRuq5LkZJgY", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problem -> {
                    assertNotNull(problem);
                    assertNotNull(problem.getTitle());
                    assertNotNull(problem.getProperties());
                    assertEquals("Validation Error", problem.getTitle());
                    assertEquals(400, problem.getStatus());
                    assertEquals(URI.create("/api/validation-error"), problem.getInstance());
                    assertEquals("New password must not be blank", problem.getProperties().get("newPassword"));
                });
    }*/

}