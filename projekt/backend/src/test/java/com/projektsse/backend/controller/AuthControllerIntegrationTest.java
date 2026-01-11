package com.projektsse.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.RefreshTokenHasher;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "MAIL_HOST=localhost",
                "MAIL_PORT=1025",
                "MAIL_USERNAME=",
                "MAIL_PASSWORD="
        }
)
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setDatasourceProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestConfiguration testConfiguration;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private RefreshTokenHasher refreshTokenHasher;

    @Test
    void registerUserTest() throws Exception {
        RegisterReq req = new RegisterReq("frankestein@gmail.com", "Xfr@nke41!g+6&4!");

        mockMvc.perform(post("/api/auth/register")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated());
    }

    @Test
    void registerUserInvalidEmailTest() throws Exception {
        RegisterReq req = new RegisterReq("invalid-email", "Xfr@nke41!g+6&4!");

        mockMvc.perform(post("/api/auth/register")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    @Test
    void registerUserWeakPasswordTest() throws Exception {
        RegisterReq req = new RegisterReq("frankestein@gmail.com", "pass123");
        mockMvc.perform(post("/api/auth/register")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("Passwort ist zu schwach"));
    }
}