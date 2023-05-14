package com.dqtri.mango.configuring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {UnauthorizedEntryPoint.class})
public class UnauthorizedEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private UnauthorizedEntryPoint authenticationEntryPoint = new UnauthorizedEntryPoint();

    @Test
    void commence() throws IOException {
        //given
        UsernameNotFoundException exception = new UsernameNotFoundException("User Authentication Error.");
        //when
        authenticationEntryPoint.commence(request, response, exception);
        //then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
