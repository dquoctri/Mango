package com.dqtri.mango.authentication.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private static final String BEARER = "Bearer ";
    
    private final JwtTokenService jwtTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            validate(authentication);
            if (authentication.getName().startsWith(BEARER)){
                String token = authentication.getName().substring(BEARER.length());
                return jwtTokenService.verifyToken(token);
            }
        } catch (UsernameNotFoundException ex){
            log.error(ex.getMessage());
        } catch (Exception e) {
            log.error("Authentication failed", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication, "Only Accepts JWT Token");
    }
}
