/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.impl;

import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.security.CoreAuthenticationToken;
import com.dqtri.mango.core.security.CoreUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenService {

    /**
     * Verify the access token.
     *
     * @param token JWT access token.
     * @return {@link CoreAuthenticationToken}
     */
    public CoreAuthenticationToken verifyToken(String token) throws ExecutionException{
        CoreUser mangoUser = new CoreUser();
        mangoUser.setEmail("username");
        CoreUserDetails coreUserDetails = new CoreUserDetails(mangoUser);
        return new CoreAuthenticationToken(coreUserDetails, coreUserDetails.getAuthorities());
    }
}
