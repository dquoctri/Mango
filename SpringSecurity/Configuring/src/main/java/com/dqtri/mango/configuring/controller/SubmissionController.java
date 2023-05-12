package com.dqtri.mango.configuring.controller;

import com.dqtri.mango.configuring.model.Submission;
import com.dqtri.mango.configuring.model.dto.SubmissionPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SubmissionController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> create(@RequestBody @Valid SubmissionPayload submissionBody) {
        Submission submission = submissionBody.toSubmission();
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }
}
