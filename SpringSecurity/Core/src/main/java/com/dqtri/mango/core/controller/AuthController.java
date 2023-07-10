/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.audit.AuditAction;
import com.dqtri.mango.core.exception.ConflictException;
import com.dqtri.mango.core.exception.LoginFailedException;
import com.dqtri.mango.core.model.BlackListRefreshToken;
import com.dqtri.mango.core.model.LoginAttempt;
import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.model.dto.payload.LoginPayload;
import com.dqtri.mango.core.model.dto.payload.RegisterPayload;
import com.dqtri.mango.core.model.dto.response.AuthenticationResponse;
import com.dqtri.mango.core.model.dto.response.ErrorResponse;
import com.dqtri.mango.core.model.dto.response.RefreshResponse;
import com.dqtri.mango.core.model.dto.response.UserResponse;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.BlackListRefreshTokenRepository;
import com.dqtri.mango.core.repository.LoginAttemptRepository;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.TokenProvider;
import com.dqtri.mango.core.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Responsible for handling user authentication-related operations")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider refreshTokenProvider;
    private final TokenProvider accessTokenProvider;
    private final UserRepository userRepository;
    private final BlackListRefreshTokenRepository blackListRefreshTokenRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    @Value("${core.auth.refresh.expirationInMs}")
    private long refreshExpirationInMs;

    @Operation(summary = "Register by providing necessary registration details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered user details",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "400", description = "The provided email or password format is invalid",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))}),
    })
    @Transactional
    @AuditAction("REGISTER")
    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterPayload register) {
        checkConflictUserEmail(register.getEmail());
        SafeguardUser safeguardUser = createSafeguardUser(register);
        SafeguardUser saved = userRepository.save(safeguardUser);
        cleanLoginAttempt(register.getEmail());
        return ResponseEntity.ok(new UserResponse(saved));
    }

    private void cleanLoginAttempt(String email) {
        Optional<LoginAttempt> byEmail = loginAttemptRepository.findByEmail(email);
        byEmail.ifPresent(loginAttemptRepository::delete);
    }

    private void checkConflictUserEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    private SafeguardUser createSafeguardUser(@NotNull RegisterPayload register) {
        SafeguardUser user = new SafeguardUser();
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.SUBMITTER);
        return user;
    }

    @Operation(summary = "Login by providing email & password credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generate a refresh token and a access token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "The provided email or password format is invalid",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid email or password credentials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "422", description = "Failed login more than 5 times",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @AuditAction("LOGIN")
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginPayload login) {
        validateLoginAttempt(login.getEmail());
        var authenticationToken = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String refreshToken = refreshTokenProvider.generateToken(authentication);
        String accessToken = accessTokenProvider.generateToken(authentication);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(new AuthenticationResponse(refreshToken, accessToken));
    }

    private void validateLoginAttempt(String email) {
        Optional<LoginAttempt> byEmail = loginAttemptRepository.findByEmail(email);
        if (byEmail.isPresent() && byEmail.get().isLockout()) {
            throw new LoginFailedException(String.format("%s has been locked due to multiple failed login attempts", email));
        }
    }

    @Operation(summary = "Generates a new access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generate a new access token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token credentials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @SecurityRequirement(name = "refresh_token")
    @GetMapping(value = "/refresh", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('REFRESH') or hasRole('REFRESH')")
    public ResponseEntity<RefreshResponse> refresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = accessTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new RefreshResponse(accessToken));
    }

    @Operation(summary = "Sign out the refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Add the token into black list", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token credentials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @SecurityRequirement(name = "refresh_token")
    @AuditAction("LOGOUT")
    @DeleteMapping("logout")
    @PreAuthorize("hasAuthority('REFRESH') or hasRole('REFRESH')")
    public ResponseEntity<Void> logout(@UserPrincipal UserDetails currentUser,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String refreshToken) {
        var token = createRefreshTokenBlackList(currentUser.getUsername(), refreshToken);
        blackListRefreshTokenRepository.save(token);
        return ResponseEntity.noContent().build();
    }

    private BlackListRefreshToken createRefreshTokenBlackList(String email, String refreshToken) {
        BlackListRefreshToken blackListRefreshToken = new BlackListRefreshToken();
        blackListRefreshToken.setEmail(email);
        blackListRefreshToken.setToken(refreshToken);
        Instant expirationDate = new Date().toInstant().plus(refreshExpirationInMs, ChronoUnit.MILLIS);
        blackListRefreshToken.setExpirationDate(expirationDate);
        return blackListRefreshToken;
    }

    @Hidden
    @PostMapping("forgot-password")
    public ResponseEntity<String> forgotPassword() {
        return ResponseEntity.ok("forgot_password");
    }

    @Hidden
    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword() {
        return ResponseEntity.ok("reset_password");
    }

    @Hidden
    @SecurityRequirement(name = "refresh_token")
    @PostMapping("change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword() {

        return ResponseEntity.ok("change_password");
    }
}
