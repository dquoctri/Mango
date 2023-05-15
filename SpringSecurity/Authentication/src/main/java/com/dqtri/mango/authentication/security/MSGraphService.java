package com.dqtri.mango.authentication.security;

//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.microsoft.graph.models.User;
//import com.microsoft.graph.requests.GraphServiceClient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.security.core.GrantedAuthority;
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
//public class MSGraphService implements InitializingBean {
public class MSGraphService {

//    private static final Integer MAX_SIZE = 1000;
//    @Value("${authentication.azure.cache.duration:60}")
//    private int cacheDuration;
//    private LoadingCache<String, String> userDetailsCached;
//
//    @Override
//    public void afterPropertiesSet() {
//        initUserDetailsCached();
//    }
//
//    private void initUserDetailsCached() {
//        this.userDetailsCached = CacheBuilder.newBuilder()
//                .softValues()
//                .maximumSize(MAX_SIZE)
//                .expireAfterWrite(cacheDuration, TimeUnit.MINUTES)
//                .build(new CacheLoader<>() {
//                           @Override
//                           public String load(@NotNull String token) {
//                               return getUserPrincipalName(token);
//                           }
//                       }
//                );
//    }
//
//    /**
//     * Get User Details for a user Token.
//     *
//     * @param token User access token.
//     * @return List of {@link GrantedAuthority}
//     * @throws ExecutionException
//     */
//    public String getUsername(String token) throws ExecutionException {
//        return userDetailsCached.get(token);
//    }
//
//    private String getUserPrincipalName(String token) {
//        log.info("Get User Details from MS Graph");
//        User me = graphServiceClient(token)
//                .me()
//                .buildRequest()
//                .select("userPrincipalName")
//                .expand("memberOf($select=displayName)")
//                .get();
//
//        return Objects.requireNonNull(me).userPrincipalName;
//    }
//
//    /**
//     * Build graph service client from the access token.
//     *
//     * @param token access token.
//     * @return {@link GraphServiceClient}.
//     */
//    public static GraphServiceClient graphServiceClient(String token) {
//        return GraphServiceClient.builder()
//                .authenticationProvider(request -> CompletableFuture.completedFuture(token))
//                .buildClient();
//    }
}
