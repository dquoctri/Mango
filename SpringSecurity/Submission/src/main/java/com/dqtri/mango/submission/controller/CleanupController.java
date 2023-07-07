/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.controller;

import com.dqtri.mango.submission.annotation.AuthenticationApiResponses;
import com.dqtri.mango.submission.audit.AuditAction;
import com.dqtri.mango.submission.exception.LockedException;
import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.model.Submission;
import com.dqtri.mango.submission.repository.SubmissionRepository;
import com.dqtri.mango.submission.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cleanup")
@RequiredArgsConstructor
@Profile({"development", "formal_test"})
@Tag(name = "Cleanup API", description = "Endpoints for cleanup data for testing develop only")
public class CleanupController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;

    @DeleteMapping
    public ResponseEntity<String> cleanUpData() {
        // Logic to clean up data
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cleanup testing user", description = "Delete a testing user.")
    @AuthenticationApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful cleanup a testing user"),
    })
    @Transactional
    @AuditAction("CLEANUP_USER")
    @DeleteMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupUsers(@PathVariable String email) {
        if ("admin1@dqtri.com".equalsIgnoreCase(email)){
            throw new LockedException("Cannot remove this user");
        }
        List<Submission> submissions = submissionRepository.findAllBySubmitterEmail(email);
        submissionRepository.deleteAll(submissions);

        Optional<SafeguardUser> byEmail = userRepository.findByEmail(email);
        byEmail.ifPresent(userRepository::delete);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Cleanup testing submission", description = "Delete a testing submission.")
    @AuthenticationApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful cleanup a testing submission"),
    })
    @Transactional
    @AuditAction("CLEANUP_SUBMISSION")
    @DeleteMapping("/submissions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupSubmissions(@PathVariable Long id) {
        Optional<Submission> byId = submissionRepository.findById(id);
        byId.ifPresent(submissionRepository::delete);

        return ResponseEntity.noContent().build();
    }
}
