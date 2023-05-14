package com.dqtri.mango.configuring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {CustomAuthenticationFilter.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<CustomAuthenticationToken> tokenArgumentCaptor;

    private CustomAuthenticationFilter filter;

    @BeforeEach
    private void setup(){
        filter = new CustomAuthenticationFilter(authenticationManager);
    }

    @Test
    public void doFilterInternal_validToken() throws ServletException, IOException {
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        CustomAuthenticationToken token = tokenArgumentCaptor.getValue();
        assertEquals("Bearer token", token.getPrincipal());
    }
}
