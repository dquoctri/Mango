package com.dqtri.mango.core.security;

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

@SpringBootTest(classes = {CoreUnauthorizedEntryPoint.class})
public class CoreUnauthorizedEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private AuthenticationEntryPoint authenticationEntryPoint = new CoreUnauthorizedEntryPoint();

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
