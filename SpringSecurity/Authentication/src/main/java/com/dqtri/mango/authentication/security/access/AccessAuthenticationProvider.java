/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.access;

import com.dqtri.mango.authentication.security.TokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.dqtri.mango.authentication.util.Constant.BEARER;

@Component
@Slf4j
@RequiredArgsConstructor
@Qualifier("accessAuthenticationProvider")
public class AccessAuthenticationProvider implements AuthenticationProvider {

    private final TokenResolver accessTokenResolver;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        assertAuthentication(authentication);
        String bearerToken = authentication.getName();
        String accessToken = bearerToken.substring(BEARER.length());
        return accessTokenResolver.verifyToken(accessToken);
    }

    private void assertAuthentication(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(AccessAuthenticationToken.class, authentication, "Only accepts access token");
        Assert.isTrue(authentication.getName().startsWith(BEARER), "Only accepts bearer token");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
