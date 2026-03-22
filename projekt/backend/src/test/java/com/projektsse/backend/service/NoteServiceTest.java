package com.projektsse.backend.service;

import com.projektsse.backend.repository.NoteRepository;
import com.projektsse.backend.repository.UserRepository;
import com.projektsse.backend.repository.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class NoteServiceTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    RestTestClient client;

    private User testUser;
    private String authToken;
    private String fingerprint;
    private String fingerprintHash;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();

        testUser = userRepository.save(new User("test@example.com", "$argon2id$v=19$m=16,t=2,p=1$T1ZsV1ZtV1ZVd1pRVG9uZw$Y2hpbGRyZW4xMjM0NTY3OA"));

        fingerprint = jwtService.generateFingerprint();
        fingerprintHash = jwtService.hashFingerprint(fingerprint);
        authToken = jwtService.generateAccessToken(testUser.getId().toString(), fingerprintHash);

    }

//    @Test
//    void testCreateNoteSuccess() {
//        client.post()
//                .uri("/api/documents")
//                .header("Authorization", "Bearer " + authToken)
//                .cookie("__Secure-Fgp", fingerprint)
//                .cookie("X-XSRF-TOKEN", "null")
//                .body(new NoteReq("Test Note", "This is a test note.", false))
//                .exchange()
//                .expectStatus().isCreated();
//    }

    @Test
    void updateNote() {
    }
}