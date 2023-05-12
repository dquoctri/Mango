package com.dqtri.mango.configuring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfiles() {
        SecurityContext sc = SecurityContextHolder.getContext();
        log.info(String.format("Logged User: %s", sc.getAuthentication().getName()));
        return ResponseEntity.ok(sc.getAuthentication());
    }
}
