package com.dqtri.mango.authentication.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
@Service
@RequiredArgsConstructor
public class MSGraphService implements InitializingBean {

    private static final Integer MAX_SIZE = 1000;
    @Value("${authentication.azure.cache.duration:60}")
    private int cacheDuration;
    private final UserDetailsService userDetailsService;
    private LoadingCache<String, UserDetails> userDetailsCached;

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
                               return getUserDetailsAndRoles(token);
                           }
                       }
                );
    }

    /**
     * Get User Details for a user Token.
     *
     * @param token User access token.
     * @return List of {@link GrantedAuthority}
     * @throws ExecutionException
     */
    public UserDetails getUserDetails(String token) throws ExecutionException {
        return userDetailsCached.get(token);
    }

    private UserDetails getUserDetailsAndRoles(String token) {
        log.info("Get User Details from MS Graph");
        User me = graphServiceClient(token)
                .me()
                .buildRequest()
                .select("userPrincipalName")
                .expand("memberOf($select=displayName)")
                .get();

        String userEmail = Objects.requireNonNull(me).userPrincipalName;
        return userDetailsService.loadUserByUsername(userEmail);
    }

    /**
     * Build graph service client from the access token.
     * @param token access token.
     * @return {@link GraphServiceClient}.
     */
    public static GraphServiceClient graphServiceClient(String token) {
        return GraphServiceClient.builder()
                .authenticationProvider(request -> CompletableFuture.completedFuture(token))
                .buildClient();
    }
}
