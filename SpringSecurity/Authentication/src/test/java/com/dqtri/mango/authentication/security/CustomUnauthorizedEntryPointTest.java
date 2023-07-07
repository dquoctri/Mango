/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {CustomUnauthorizedEntryPoint.class})
public class CustomUnauthorizedEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private AuthenticationEntryPoint authenticationEntryPoint = new CustomUnauthorizedEntryPoint();

    @Test
    void commence() throws IOException, ServletException {
        //given
        UsernameNotFoundException exception = new UsernameNotFoundException("User Authentication Error.");
        //when
        authenticationEntryPoint.commence(request, response, exception);
        //then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
