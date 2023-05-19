package com.dqtri.mango.core.security;

import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.model.enums.Role;
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

@SpringBootTest(classes = {CoreAuthenticationFilter.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoreAuthenticationFilterTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<CoreAuthenticationToken> tokenArgumentCaptor;

    private CoreAuthenticationFilter authenticationFilter;

    @BeforeEach
    public void setup() {
        authenticationFilter = new CoreAuthenticationFilter(authenticationManager);
    }

    @Test
    public void doFilterInternal_givenNullToken_thenNoAuthentication() throws ServletException, IOException {
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
    public void doFilterInternal_givenNoBearerToken_thenNoAuthentication(String token) throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        verifyNoInteractions(authenticationManager);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void doFilterInternal_givenInvalidBearerToken_() throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        CoreAuthenticationToken token = tokenArgumentCaptor.getValue();
        assertEquals("Bearer token", token.getPrincipal());
    }

    @Test
    void doFilterInternal_bearerToken_returnAuthenticated() throws ServletException, IOException {
        //given
        CoreUser coreUser = new CoreUser();
        coreUser.setEmail("core@email.com");
        coreUser.setPassword("core@email");
        coreUser.setRole(Role.SUBMITTER);

        CoreUserDetails userDetails = new CoreUserDetails(coreUser);
        Authentication authentication = new CoreAuthenticationToken(userDetails, userDetails.getAuthorities());
        CoreAuthenticationToken jwtAuthenticationToken = new CoreAuthenticationToken("Bearer token");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authenticationManager.authenticate(jwtAuthenticationToken)).thenReturn(authentication);
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());
        CoreAuthenticationToken token = tokenArgumentCaptor.getValue();
        assertEquals("Bearer token", token.getPrincipal());
    }

    @Test
    void doFilterInternal_mockThrowException() throws ServletException, IOException {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        CoreAuthenticationToken jwtAuthenticationToken = new CoreAuthenticationToken("Bearer token");
        when(authenticationManager.authenticate(jwtAuthenticationToken))
                .thenThrow(Mockito.mock(AuthenticationException.class));
        //when
        authenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        //then
    }
}
