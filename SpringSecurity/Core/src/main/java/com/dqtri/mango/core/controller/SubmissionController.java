/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.model.Submission;
import com.dqtri.mango.core.model.dto.payload.SubmissionPayload;
import com.dqtri.mango.core.repository.SubmissionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class SubmissionController {

    private final SubmissionRepository submissionRepository;

    @GetMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(submissionRepository.findAll());
    }

    @Transactional
    @PostMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> create(@RequestBody @Valid SubmissionPayload submissionPayload) {
        Submission submission = submissionPayload.toSubmission();
        Submission save = submissionRepository.save(submission);
        return ResponseEntity.ok(save);
    }
}
