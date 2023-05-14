package com.dqtri.mango.configuring.controller;

import com.dqtri.mango.configuring.model.Submission;
import com.dqtri.mango.configuring.model.dto.SubmissionPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins="http://localhost:9000", maxAge = 3600)
public class SubmissionController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> create(@RequestBody @Valid SubmissionPayload submissionBody) {
        SecurityContext sc = SecurityContextHolder.getContext();
        log.info(String.format("Logged User: %s", sc.getAuthentication().getName()));
        Submission submission = submissionBody.toSubmission();
        submission.setPk(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Source-Id", submission.getPk().toString());
        headers.add("ERROR_CODE", "201");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(submission);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid SubmissionPayload submissionBody) {
        if (id != 1L){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found entity by id: " + id);
        }
        Submission submission = submissionBody.toSubmission();
        return ResponseEntity.ok(submission);
    }
}
