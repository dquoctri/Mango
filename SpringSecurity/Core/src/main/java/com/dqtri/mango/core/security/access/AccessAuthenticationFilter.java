/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.access;

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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
public class AccessAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (isPreflightRequest(request) || isRefreshRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String accessToken = getAuthorizationToken(request);
            validateAndAuthentication(accessToken);
//            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
//            securityContextRepository.saveContext(context, request, response);
        } catch (ProviderNotFoundException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Could not set authentication in security context", e);
        }
        filterChain.doFilter(request, response);
    }

    private void validateAndAuthentication(String accessToken) {
        if (!StringUtils.hasText(accessToken) || !validateToken(accessToken)) {
            return;
        }
        AccessAuthenticationToken accessAuthenticationToken = new AccessAuthenticationToken(accessToken);
        Authentication authentication = authenticationManager.authenticate(accessAuthenticationToken);
        log.debug("Logging in with [{}]", authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
