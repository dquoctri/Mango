/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.refresh;

import com.dqtri.mango.core.repository.BlackListRefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.dqtri.mango.core.util.Constant.getAuthorizationToken;
import static com.dqtri.mango.core.util.Constant.isPreflightRequest;
import static com.dqtri.mango.core.util.Constant.isRefreshRequest;
import static com.dqtri.mango.core.util.Constant.validateToken;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    private final BlackListRefreshTokenRepository blackListRefreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isPreflightRequest(request) && isRefreshRequest(request)) {
            try {
                String refreshToken = getAuthorizationToken(request);
                if (StringUtils.hasText(refreshToken) && validateToken(refreshToken) && !isTokenInBlackList(refreshToken)) {
                    var refreshAuthenticationToken = new RefreshAuthenticationToken(refreshToken);
                    Authentication authentication = authenticationManager.authenticate(refreshAuthenticationToken);
                    log.debug("Logging in with [{}]", authentication.getPrincipal());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    SecurityContextHolder.s.getContext().
                }
            } catch (ProviderNotFoundException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error("Could not set authentication in security context", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isTokenInBlackList(String token) {
        return blackListRefreshTokenRepository.existsByToken(token);
    }
}