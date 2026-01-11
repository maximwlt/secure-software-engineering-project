package com.projektsse.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projektsse.backend.config.JwtFilter;
import com.projektsse.backend.controller.AuthController;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = EnableWebSecurity.class

        )
)
public class RegistrierungTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void register_withValidData_returnsCreated() throws Exception {
        RegisterReq registerRequest = new RegisterReq("frankestein@gmail.com", "Xfr@nke41!g+6&4");
        mockMvc.perform(post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(registerRequest)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.message").value("Benutzer erfolgreich registriert. Bitte überprüfen Sie Ihre E-Mails zur Verifizierung."));

        verify(userService, times(1)).registerUser(any());
    }

}
