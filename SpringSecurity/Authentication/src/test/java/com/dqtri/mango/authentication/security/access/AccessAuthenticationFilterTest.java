/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.access;

import com.dqtri.mango.authentication.model.SafeguardUser;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.security.AppUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AccessAuthenticationFilter.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccessAuthenticationFilterTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<AccessAuthenticationToken> tokenArgumentCaptor;

    private AccessAuthenticationFilter authenticationFilter;

    @BeforeEach
    public void setup() {
        authenticationFilter = new AccessAuthenticationFilter(authenticationManager);
    }

    @Test
    void doFilterInternal_givenNullToken_thenNoAuthentication() throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        verifyNoInteractions(authenticationManager);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "NotBearer", "Basic token"})
     void doFilterInternal_givenNoBearerToken_thenNoAuthentication(String token) throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        verifyNoInteractions(authenticationManager);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
     void doFilterInternal_givenInvalidBearerToken_bearerToken() throws ServletException, IOException {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        AccessAuthenticationToken token = tokenArgumentCaptor.getValue();
        assertEquals("Bearer token", token.getPrincipal());
    }

    @Test
    void doFilterInternal_bearerToken_returnAuthenticated() throws ServletException, IOException {
        //given
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail("safeguard@email.com");
        safeguardUser.setPassword("safeguard@email");
        safeguardUser.setRole(Role.SUBMITTER);

        AppUserDetails userDetails = new AppUserDetails(safeguardUser);
        Authentication authentication = new AccessAuthenticationToken(userDetails, userDetails.getAuthorities());
        AccessAuthenticationToken jwtAuthenticationToken = new AccessAuthenticationToken("Bearer token");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authenticationManager.authenticate(jwtAuthenticationToken)).thenReturn(authentication);
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());
        AccessAuthenticationToken token = tokenArgumentCaptor.getValue();
        assertEquals("Bearer token", token.getPrincipal());
    }

    @Test
    void doFilterInternal_mockThrowException() throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        AccessAuthenticationToken jwtAuthenticationToken = new AccessAuthenticationToken("Bearer token");
        when(authenticationManager.authenticate(jwtAuthenticationToken))
                .thenThrow(Mockito.mock(AuthenticationException.class));
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        //TODO:
    }
}
