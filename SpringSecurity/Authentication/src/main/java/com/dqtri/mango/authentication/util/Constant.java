/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

@Slf4j
public class Constant {

    private Constant(){}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String BASIC = "Basic ";

    public static boolean isPreflightRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod());
    }

    public static boolean isRefreshRequest(HttpServletRequest request) {
        return "/auth/refresh".equals(request.getServletPath())
                || "/auth/logout".equals(request.getServletPath());
    }

    public static String getAuthorizationToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    public static boolean validateToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            log.error("Authentication Token is missing");
            return false;
        }

        if (accessToken.startsWith(BASIC)) {
            log.info("User logged with basic authentication");
            return false;
        }

        if (!accessToken.startsWith(BEARER)) {
            log.error("Authentication token is not bearer");
            return false;
        }
        return true;
    }
}
