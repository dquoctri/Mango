/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.access;

import com.dqtri.mango.authentication.model.SafeguardUser;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.security.AppUserDetails;
import com.dqtri.mango.authentication.security.TokenResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AccessAuthenticationFilterTest.class})
class AccessAuthenticationProviderTest {

    @Mock
    TokenResolver tokenResolver;

    @InjectMocks
    AccessAuthenticationProvider authenticationProvider;

    @Test
    void authenticate_givenToken_thenSuccess() {
        SafeguardUser safeguardUser = createSubmitterUser();
        AppUserDetails appUserDetails = new AppUserDetails(safeguardUser);
        when(tokenResolver.verifyToken("header.payload.signature"))
                .thenReturn(new AccessAuthenticationToken(appUserDetails, appUserDetails.getAuthorities()));
        var customAuthenticationToken = new AccessAuthenticationToken("Bearer header.payload.signature");
        Authentication authenticate = authenticationProvider.authenticate(customAuthenticationToken);

        //test
        assertThat(authenticate).isNotNull();
        assertThat(authenticate).isInstanceOf(AccessAuthenticationToken.class);
        assertThat(authenticate.isAuthenticated()).isTrue();
        assertThat(authenticate.getPrincipal()).isNotNull();
        assertThat(authenticate.getPrincipal()).isInstanceOf(AppUserDetails.class);
        AppUserDetails principal = (AppUserDetails) authenticate.getPrincipal();
        assertThat(principal.getUsername()).isEqualTo("submitter@dqtri.com");
    }

    private SafeguardUser createSubmitterUser() {
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail("submitter@dqtri.com");
        safeguardUser.setPassword("submitter");
        safeguardUser.setRole(Role.SUBMITTER);
        return safeguardUser;
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer abc", "Bearer test-failed"})
    void authenticate_givenInvalidToken_thenNull(String accessToken) {
        AccessAuthenticationToken accessAuthenticationToken = new AccessAuthenticationToken(accessToken);
        Authentication authenticate = authenticationProvider.authenticate(accessAuthenticationToken);
        //test
        assertThat(authenticate).isNull();
    }

    @Test
    void authenticate_givenNullAuthentication_thenException() {
        Executable executable = () -> authenticationProvider.authenticate(null);
        //test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                executable,
                "Authentication is missing"
        );
        assertThat(exception.getMessage()).isEqualTo("Authentication is missing");
    }

    @Test
    void authenticate_givenNullToken_thenException() {
        AccessAuthenticationToken accessAuthenticationToken = new AccessAuthenticationToken((String)null);
        Executable executable = () -> authenticationProvider.authenticate(accessAuthenticationToken);
        //test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                executable,
                "Authentication principal is missing"
        );
        assertThat(exception.getMessage()).isEqualTo("Authentication principal is missing");
    }

    @Test
    void authenticate_givenUsernamePasswordToken_thenNull() {
        var token = new UsernamePasswordAuthenticationToken("submitter", "submitter");
        Executable executable = () -> authenticationProvider.authenticate(token);
        //test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                executable,
                "Only accepts access token"
        );
        assertThat(exception.getMessage()).startsWith("Only accepts access token");
    }

    @Test
    void support_givenCustomToken_thenTrue() {
        boolean supports = authenticationProvider.supports(AccessAuthenticationToken.class);
        assertThat(supports).isTrue();
    }

    @Test
    void support_givenUsernamePasswordToken_thenFalse() {
        boolean supports = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        assertThat(supports).isFalse();
    }
}
