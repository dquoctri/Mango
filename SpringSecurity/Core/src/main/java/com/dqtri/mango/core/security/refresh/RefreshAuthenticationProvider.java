/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.refresh;

import com.dqtri.mango.core.security.TokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.dqtri.mango.core.util.Constant.BEARER;

@Component
@Slf4j
@RequiredArgsConstructor
@Qualifier("refreshAuthenticationProvider")
public class RefreshAuthenticationProvider implements AuthenticationProvider {
    private final TokenResolver refreshTokenResolver;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            validate(authentication);
            String refreshToken = authentication.getName().substring(BEARER.length());
            return refreshTokenResolver.verifyToken(refreshToken);
        } catch (Exception e) {
            log.error("Authentication failed", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(RefreshAuthenticationToken.class, authentication, "Only Accepts Core Token");
        Assert.isTrue(authentication.getName().startsWith(BEARER), "Only Accepts Bearer Token");
    }
}