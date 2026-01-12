package com.projektsse.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projektsse.backend.controller.dto.LoginReq;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.User;
import com.projektsse.backend.service.JwtService;
import com.projektsse.backend.service.RefreshTokenHasher;
import com.projektsse.backend.service.TokenService;
import com.projektsse.backend.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User(
                "frankestein@gmail.com",
                passwordEncoder.encode("Xfr@nke41!g+6&4!")
        );
        userRepository.save(user);
    }


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

//    @Test
//    void loginUserTest() throws Exception {
//        LoginReq loginReq = new LoginReq(
//                "frankestein@gmail.com",
//                "Xfr@nke41!g+6&4!"
//        );
//
//        mockMvc.perform(post("/api/auth/login")
//                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
//                                .content(objectMapper.writeValueAsString(loginReq)))
//               .andExpect(status().isOk())
//
//               .andExpect(jsonPath("$.accessToken").exists())
//               .andExpect(jsonPath("$.accessToken").isNotEmpty())
//
//               .andExpect(header().exists("Set-Cookie"))
//               .andExpect(header().string(
//                       "Set-Cookie",
//                       Matchers.containsString("REFRESH_TOKEN=")
//               ))
//               .andExpect(header().string(
//                       "Set-Cookie",
//                       Matchers.containsString("HttpOnly")
//               ));
//    }
}