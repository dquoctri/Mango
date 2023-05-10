/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.csrf;

import com.dqtri.mango.authentication.controller.AuthController;
import com.dqtri.mango.authentication.controller.SubmissionController;
import com.dqtri.mango.authentication.repository.SubmissionRepository;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.TokenProvider;
import com.dqtri.mango.authentication.security.csrf.config.SecurityWithoutCsrfConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {SecurityWithoutCsrfConfig.class})
@WebMvcTest({AuthController.class, SubmissionController.class})
public class CsrfDisabledIntegrationTest extends CsrfAbstractIntegrationTest {


    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SubmissionRepository submissionRepository;

//    @BeforeEach
//    public void setup() {
//
//    }

    @Autowired
    protected MockMvc mvc;

    @Test
    public void login_givenNotAuth_thenUnauthorized() throws Exception {
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(createTestLoginPayload())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "abc", roles = "SUBMITTER")
    public void submission_givenNotAuth_thenUnauthorized() throws Exception {
        mvc.perform(get("/submissions")
        ).andExpect(status().isUnauthorized());
    }
}
