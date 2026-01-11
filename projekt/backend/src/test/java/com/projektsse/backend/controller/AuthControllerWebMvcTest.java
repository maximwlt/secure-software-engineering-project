package com.projektsse.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projektsse.backend.config.JwtFilter;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = EnableWebSecurity.class
        )
)
class AuthControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenService tokenService;


    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void register_success() throws Exception {

        doNothing().when(userService).registerUser(any());

        RegisterReq req = new RegisterReq(
                "frankestein@gmail.com",
                "Xfr@nke41!g+6&4"
        );

        mockMvc.perform(post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.message")
                                  .value("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung."));
    }

    @Test
    void register_invalidEmail() throws Exception {

        RegisterReq req = new RegisterReq(
                "invalid-email",
                "Xfr@nke41!g+6&4"
        );

        mockMvc.perform(post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.email")
                                  .value("Ungültige E-Mail-Adresse"));
    }

    @Test
    void register_weakPassword() throws Exception {

        RegisterReq req = new RegisterReq(
                "frankestein@gmail.com",
                "pass123"
        );

        mockMvc.perform(post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.password")
                                  .value("Passwort ist zu schwach"));
    }
}