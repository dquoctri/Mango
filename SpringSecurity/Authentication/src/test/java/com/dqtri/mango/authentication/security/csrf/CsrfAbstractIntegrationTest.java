/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.csrf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public abstract class CsrfAbstractIntegrationTest {

    @Autowired
    protected MockMvc mvc;

    protected String createTestLoginPayload() throws JsonProcessingException {
//        LoginPayload loginPayload = new LoginPayload();
//        loginPayload.setEmail("test@mango.dqtri.com");
//        loginPayload.setPassword("*******");
        return new ObjectMapper().writeValueAsString(new Object());
    }

}
