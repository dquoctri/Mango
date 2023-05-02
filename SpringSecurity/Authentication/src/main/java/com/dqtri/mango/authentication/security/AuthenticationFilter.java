package com.dqtri.mango.authentication.security;

import java.io.IOException;

import com.dqtri.mango.authentication.security.jwt.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String BASIC = "Basic ";

    private final AuthenticationManager authenticationManager;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isPreflightRequest(request)) {
            try {
                String accessToken = getAuthorizationToken(request);
                if (StringUtils.hasText(accessToken) && tokenValid(accessToken)) {
                    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(accessToken);
                    Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ProviderNotFoundException ex) {
                log.error("Could not set authentication in security context", ex);
            } catch (Exception e){
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

    private boolean tokenValid(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            log.error("Authentication Token is missing");
            return false;
        }

        if (accessToken.startsWith(BASIC)) {
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
