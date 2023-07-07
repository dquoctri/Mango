/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuditTrailFilter extends OncePerRequestFilter {

    private static final String COMMENTS_HEADER = "audit_info.comments";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuditInfoHolder instance = AuditInfoHolder.getInstance();
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setUri(request.getRequestURI());
        auditInfo.setMethod(request.getMethod());
        auditInfo.setEmail(request.getRemoteUser());
        auditInfo.setDescription(request.getHeader(COMMENTS_HEADER));
        instance.setCurrentContext(auditInfo);
        filterChain.doFilter(request, response);
    }
}
