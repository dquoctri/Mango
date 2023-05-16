/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.exception.ConflictException;
import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.model.dto.payload.LoginPayload;
import com.dqtri.mango.core.model.dto.payload.RegisterPayload;
import com.dqtri.mango.core.model.dto.response.TokenResponse;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.TokenProvider;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody @Valid LoginPayload login) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenResponse tokenResponse = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody @Valid RegisterPayload register) {
        checkConflictUserEmail(register.getEmail());
        CoreUser saved = userRepository.save(createCoreUser(register));
        return ResponseEntity.ok(saved);
    }

    private void checkConflictUserEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    private CoreUser createCoreUser(@NotNull RegisterPayload register) {
        CoreUser user = new CoreUser();
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.SUBMITTER);
        return user;
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword() {
        return ResponseEntity.ok("forgot_password");
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword() {
        return ResponseEntity.ok("reset_password");
    }

    @PostMapping("change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword() {

        return ResponseEntity.ok("change_password");
    }
}
