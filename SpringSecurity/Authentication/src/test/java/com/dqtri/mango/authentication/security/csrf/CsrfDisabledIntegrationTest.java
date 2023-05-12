/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.csrf;

import com.dqtri.mango.authentication.controller.UserController;
import com.dqtri.mango.authentication.model.dto.UserCreatingPayload;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.csrf.config.SecurityWithoutCsrfConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class},
        includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityWithoutCsrfConfig.class))
public class CsrfDisabledIntegrationTest extends CsrfAbstractIntegrationTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "abc", roles = "SUBMITTER")
    public void login_givenNotAuth_thenUnauthorized() throws Exception {
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createSampleUserPayload())
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "abc", roles = "SUBMITTER")
    public void submission_givenNotAuth_thenUnauthorized() throws Exception {
        mvc.perform(get("/users")
        ).andExpect(status().isForbidden());
    }

    public String createSampleUserPayload() throws JsonProcessingException {
        UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
        userCreatingPayload.setEmail("test@mango.dqtri.com");
        userCreatingPayload.setPassword("*******");
        userCreatingPayload.setRole(Role.SUBMITTER);
        return new ObjectMapper().writeValueAsString(userCreatingPayload);
    }
}
