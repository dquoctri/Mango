/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.controller;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.model.dto.LoginPayload;
import com.dqtri.mango.authentication.model.dto.RegisterPayload;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.TokenProvider;
import com.dqtri.mango.authentication.security.models.TokenResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
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
        MangoUser user = new MangoUser();
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.SUBMITTER);
        MangoUser saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("forgot_password")
    public ResponseEntity<?> forgotPassword(){
        return ResponseEntity.ok(List.of("payload1a", "payload1b"));
    }

    @PostMapping("reset_password")
    public ResponseEntity<?> resetPassword(){
        return ResponseEntity.ok(List.of("payload1a", "payload1b"));
    }

    @PostMapping("change_password")
    public ResponseEntity<?> changePassword(){
        return ResponseEntity.ok(List.of("payload1a", "payload1b"));
    }
}
