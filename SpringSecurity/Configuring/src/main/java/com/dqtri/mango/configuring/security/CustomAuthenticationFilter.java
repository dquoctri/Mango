package com.dqtri.mango.configuring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER = "Bearer ";
    private static final String BASIC_TOKEN_PREFIX = "Basic ";

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isPreflightRequest(request)) {
            try {
                String accessToken = getAuthorizationToken(request);
                if (StringUtils.hasText(accessToken) && tokenValidFormat(accessToken)) {
                    CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(accessToken);
                    Authentication authentication = authenticationManager.authenticate(customAuthenticationToken);
                    if (authentication != null) {
                        log.debug("Logging in with [{}]", authentication.getPrincipal());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error("Could not set authentication in security context", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod());
    }

    private String getAuthorizationToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    private boolean tokenValidFormat(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            log.error("Authentication Token is missing");
            return false;
        }

        if (accessToken.startsWith(BASIC_TOKEN_PREFIX)) {
            log.info("User logged with basic authentication");
            return false;
        }

        if (!accessToken.startsWith(BEARER)) {
            log.error("Authentication token is invalid");
            return false;
        }
        return true;
    }
}
