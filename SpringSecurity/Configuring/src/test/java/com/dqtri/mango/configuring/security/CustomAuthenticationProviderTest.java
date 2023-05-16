package com.dqtri.mango.configuring.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {CustomAuthenticationProvider.class})
public class CustomAuthenticationProviderTest {

    @InjectMocks
    AuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();

    @Test
    public void authenticate_givenToken_thenSuccess() {
        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken("Bearer header.payload.signature");
        Authentication authenticate = authenticationProvider.authenticate(customAuthenticationToken);

        //test
        assertThat(authenticate).isNotNull();
        assertThat(authenticate).isInstanceOf(CustomAuthenticationToken.class);
        assertThat(authenticate.isAuthenticated()).isTrue();
        assertThat(authenticate.getPrincipal()).isNotNull();
        assertThat(authenticate.getPrincipal()).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();
        assertThat(principal.getUsername()).isEqualTo("submitter@mango.dqtri.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer abc", "Bearer test-failed"})
    public void authenticate_givenInvalidToken_thenNull(String accessToken) {
        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(accessToken);
        Authentication authenticate = authenticationProvider.authenticate(customAuthenticationToken);
        //test
        assertThat(authenticate).isNull();
    }

    @Test
    public void authenticate_givenNullAuthentication_thenException() {
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
    public void authenticate_givenNullToken_thenException() {
        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(null);
        Executable executable = () -> authenticationProvider.authenticate(customAuthenticationToken);
        //test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                executable,
                "Authentication principal is missing"
        );
        assertThat(exception.getMessage()).isEqualTo("Authentication principal is missing");
    }

    @Test
    public void authenticate_givenUsernamePasswordToken_thenNull() {
        var token = new UsernamePasswordAuthenticationToken("submitter", "submitter");
        Executable executable = () -> authenticationProvider.authenticate(token);
        //test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                executable,
                "Only Accepts Custom Token"
        );
        assertThat(exception.getMessage()).startsWith("Only Accepts Custom Token");
    }

    @Test
    public void support_givenCustomToken_thenTrue() {
        boolean supports = authenticationProvider.supports(CustomAuthenticationToken.class);
        assertThat(supports).isTrue();
    }

    @Test
    public void support_givenUsernamePasswordToken_thenFalse() {
        boolean supports = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        assertThat(supports).isFalse();
    }
}
