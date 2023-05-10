/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.csrf;

import com.dqtri.mango.authentication.controller.AuthController;
import com.dqtri.mango.authentication.model.dto.LoginPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class CsrfAbstractIntegrationTest {

//    @MockBean
//    private WebApplicationContext context;
//
//    @MockBean
//    private Filter securityFilter;

//    @Autowired
//    protected MockMvc mvc;

//    @BeforeEach
//    public void setup() {
//        mvc = MockMvcBuilders.webAppContextSetup(context)
//                .addFilters(securityFilter)
//                .build();
//    }

    protected String createTestLoginPayload() throws JsonProcessingException {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setEmail("test@mango.dqtri.com");
        loginPayload.setPassword("*******");
        return new ObjectMapper().writeValueAsString(loginPayload);
    }

}
