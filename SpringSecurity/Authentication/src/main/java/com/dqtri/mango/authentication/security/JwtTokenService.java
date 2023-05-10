/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.model.MangoUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenService {

    private final MSGraphService msGraphService;

    /**
     * Verify the access token.
     *
     * @param token JWT access token.
     * @return {@link JwtAuthenticationToken}
     */
    public JwtAuthenticationToken verifyToken(String token) throws ExecutionException, JWTVerificationException {
        verifyTokenExpiration(token);
        String username = msGraphService.getUsername(token);
        MangoUser mangoUser = new MangoUser();
        mangoUser.setEmail(username);
        MangoUserDetails mangoUserDetails = new MangoUserDetails(mangoUser);
        return new JwtAuthenticationToken(mangoUserDetails, mangoUserDetails.getAuthorities());
    }

    private void verifyTokenExpiration(String token) throws JWTVerificationException {
        //Note: The token used for MS Graph, We should not validate signature of this token. Leave that for MS Graph.
        //https://docs.microsoft.com/en-us/answers/questions/318741/graphapi-cannot-validate-access-token-signature.html
        DecodedJWT tokenDecoded = JWT.decode(token);
        if (tokenDecoded.getExpiresAt().before(new Date())) {
            throw new JWTVerificationException("Access token is expired");
        }
    }
}
