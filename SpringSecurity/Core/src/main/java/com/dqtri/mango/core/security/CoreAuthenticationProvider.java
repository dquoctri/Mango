/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security;

import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Primary
@RequiredArgsConstructor
@Component
public class CoreAuthenticationProvider implements AuthenticationProvider {

    private static final String BEARER = "Bearer ";

    private final JwtTokenService jwtTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            validate(authentication);
            String accessToken = authentication.getName().substring(BEARER.length());
            return jwtTokenService.verifyToken(accessToken);
        } catch (Exception e) {
            log.error("Authentication failed", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CoreAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(CoreAuthenticationToken.class, authentication, "Only Accepts JWT Token");
        Assert.isTrue(authentication.getName().startsWith(BEARER), "Only Accepts Bearer Token");
    }
}
