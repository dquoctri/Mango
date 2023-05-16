/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.controller;

import com.dqtri.mango.authentication.exception.ConflictException;
import com.dqtri.mango.authentication.model.CoreUser;
import com.dqtri.mango.authentication.model.dto.payload.LoginPayload;
import com.dqtri.mango.authentication.model.dto.payload.RegisterPayload;
import com.dqtri.mango.authentication.model.dto.response.TokenResponse;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.TokenProvider;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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

        CoreUser user = new CoreUser();
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.SUBMITTER);
        CoreUser saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    private void checkConflictUserEmail(String email) {
        Optional<CoreUser> existedUser = userRepository.findByEmail(email);
        if (existedUser.isPresent()) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    @PostMapping("forgot_password")
    public ResponseEntity<?> forgotPassword() {
        return ResponseEntity.ok("forgot_password");
    }

    @PostMapping("reset_password")
    public ResponseEntity<?> resetPassword() {
        return ResponseEntity.ok("reset_password");
    }

    @PostMapping("change_password")
    public ResponseEntity<?> changePassword() {
        return ResponseEntity.ok("change_password");
    }
}
