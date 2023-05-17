/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.impl;

import com.dqtri.mango.submission.security.CoreAuthenticationToken;
import com.dqtri.mango.submission.security.CoreUserDetails;
import com.dqtri.mango.submission.security.TokenResolver;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class MSGraphTokenResolver implements TokenResolver {
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication verifyToken(String token)  {
        String email = getUserPrincipalName(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails instanceof CoreUserDetails coreUser) {
            return new CoreAuthenticationToken(coreUser, coreUser.getAuthorities());
        }
        return new CoreAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private String getUserPrincipalName(String token) {
        log.info("Get User Details from MS Graph");
        User me = graphServiceClient(token)
                .me()
                .buildRequest()
                .select("userPrincipalName")
                .expand("memberOf($select=displayName)")
                .get();

        return Objects.requireNonNull(me).userPrincipalName;
    }

    /**
     * Build graph service client from the access token.
     *
     * @param token access token.
     * @return {@link GraphServiceClient}.
     */
    public static GraphServiceClient graphServiceClient(String token) {
        return GraphServiceClient.builder()
                .authenticationProvider(request -> CompletableFuture.completedFuture(token))
                .buildClient();
    }
}
