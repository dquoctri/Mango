/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.model.enums.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
public abstract class AbstractIntegrationTest {

    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    protected MockMvc mvc;

    protected String createPayloadJson(Object value) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(value);
    }

    protected RequestPostProcessor mockAdminUser() {
        return mockUser("admin@mango.dqtri.com", Role.ADMIN);
    }

    protected RequestPostProcessor mockSubmitterUser() {
        return mockUser("manager@mango.dqtri.com", Role.SUBMITTER);
    }

    protected RequestPostProcessor mockManagerUser() {
        return mockUser("manager@mango.dqtri.com", Role.MANAGER);
    }

    protected RequestPostProcessor mockSpecialistUser() {
        return mockUser("specialist@mango.dqtri.com", Role.SPECIALIST);
    }

    protected RequestPostProcessor mockInactiveUser() {
        return mockUser("none@mango.dqtri.com", Role.NONE);
    }

    protected RequestPostProcessor mockUser(@NotNull String email, @NotNull Role role){
        return user(email).password("********").roles(role.name());
    }
}
