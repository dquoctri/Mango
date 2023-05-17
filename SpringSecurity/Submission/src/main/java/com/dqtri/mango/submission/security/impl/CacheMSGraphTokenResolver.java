/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.impl;

import com.dqtri.mango.submission.security.CoreAuthenticationToken;
import com.dqtri.mango.submission.security.CoreUserDetails;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.dqtri.mango.submission.security.TokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class CacheMSGraphTokenResolver implements TokenResolver, InitializingBean {
    private static final Integer MAX_SIZE = 152;

    private final UserDetailsService userDetailsService;
    @Value("${submission.azure.cache.duration:5}")
    private int cacheDuration = 5;
    
    private LoadingCache<String, UserDetails> userDetailsCached;

    @Override
    public Authentication verifyToken(String token) throws Exception {
        UserDetails userDetails = getUserDetailsFromToken(token);
        return new CoreAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public void afterPropertiesSet() {
        initUserDetailsCached();
    }

    private void initUserDetailsCached() {
        this.userDetailsCached = CacheBuilder.newBuilder()
                .softValues()
                .maximumSize(MAX_SIZE)
                .expireAfterWrite(cacheDuration, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public UserDetails load(@NotNull String token) {
                               return getUserPrincipalName(token);
                           }
                       }
                );
    }

    /**
     * Get User Details for a user Token.
     *
     * @param token User access token.
     */
    public UserDetails getUserDetailsFromToken(String token) throws ExecutionException {
        return userDetailsCached.get(token);
    }

    private UserDetails getUserPrincipalName(String token) {
        log.info("Get User Details from MS Graph");
        User me = graphServiceClient(token)
                .me()
                .buildRequest()
                .select("userPrincipalName")
                .expand("memberOf($select=displayName)")
                .get();
        String email = Objects.requireNonNull(me).userPrincipalName;
        return userDetailsService.loadUserByUsername(email);
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
