/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.security.TokenProvider;
import com.dqtri.mango.authentication.security.models.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MangoTokenProvider implements TokenProvider {
    @Override
    public TokenResponse generateToken(Authentication authentication) {

        return null;
    }
}
