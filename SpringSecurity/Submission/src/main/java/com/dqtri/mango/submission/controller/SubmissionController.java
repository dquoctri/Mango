/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.controller;

import com.dqtri.mango.submission.annotation.AuthenticationApiResponses;
import com.dqtri.mango.submission.annotation.GlobalApiResponses;
import com.dqtri.mango.submission.annotation.NotFound404ApiResponses;
import com.dqtri.mango.submission.annotation.Validation400ApiResponses;
import com.dqtri.mango.submission.exception.LockedException;
import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.model.Submission;
import com.dqtri.mango.submission.model.dto.SubmissionPageCriteria;
import com.dqtri.mango.submission.model.dto.payload.ApprovalPayload;
import com.dqtri.mango.submission.model.dto.payload.AssignPayload;
import com.dqtri.mango.submission.model.dto.payload.SubmissionPayload;
import com.dqtri.mango.submission.model.dto.response.ErrorResponse;
import com.dqtri.mango.submission.model.dto.response.SubmissionResponse;
import com.dqtri.mango.submission.model.enums.Role;
import com.dqtri.mango.submission.model.enums.Status;
import com.dqtri.mango.submission.repository.SubmissionRepository;
import com.dqtri.mango.submission.repository.UserRepository;
import com.dqtri.mango.submission.security.AppUserDetails;
import com.dqtri.mango.submission.security.UserPrincipal;
import com.dqtri.mango.submission.util.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Predicate;

@RestController
@RequiredArgsConstructor
@AuthenticationApiResponses
@GlobalApiResponses
@SecurityRequirement(name = "access_token")
@Tag(name = "Submission API", description = "Endpoints for managing user submissions")
public class SubmissionController {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Operation(summary = "Get submissions", description = "Retrieve a paginated list of submissions")
    @Validation400ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval a paginated list of submissions",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class, subTypes = SubmissionResponse.class))})
    })
    @Transactional(readOnly = true)
    @GetMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<Page<SubmissionResponse>> getSubmissions(@Valid SubmissionPageCriteria pageCriteria) {
        Pageable pageable = pageCriteria.toPageable("pk");
        Page<Submission> page = submissionRepository
                .findByStatus(pageCriteria.getStatus(), pageCriteria.getSubmitterEmail(), pageable);
        List<SubmissionResponse> submissionResponses = SubmissionResponse.buildFromSubmissions(page.getContent());
        Page<SubmissionResponse> pagination = Helper.createPagination(submissionResponses, page);
        return ResponseEntity.ok(pagination);
    }

    @Operation(summary = "Get submission", description = "Retrieve a submission based on the provided ID")
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of submission",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionResponse.class))})
    })
    @Transactional(readOnly = true)
    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable("id") Long id) {
        Submission submission = getSubmissionOrElseThrow(id);
        return ResponseEntity.ok(new SubmissionResponse(submission));
    }

    @Operation(summary = "Create submission", description = "Create a new submission with the provided details")
    @Validation400ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful creation of submission",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionResponse.class))})
    })
    @Transactional
    @PostMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<SubmissionResponse> createSubmission(@UserPrincipal AppUserDetails currentUser,
                                                               @RequestBody @Valid SubmissionPayload submissionPayload) {
        Submission submission = submissionPayload.toSubmission();
        submission.setSubmitter(currentUser.getSafeguardUser());
        Submission saved = submissionRepository.save(submission);
        return new ResponseEntity<>(new SubmissionResponse(saved), HttpStatus.CREATED);
    }

    @Operation(summary = "Update submission", description = "Update an existing submission with the provided details")
    @Validation400ApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of submission",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Submission.class))}),
            @ApiResponse(responseCode = "423", description = "The submission is assigned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Transactional
    @PutMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER') and hasPermission(#id, @submissionOwner)")
    public ResponseEntity<SubmissionResponse> updateSubmission(@PathVariable("id") Long id,
                                                       @RequestBody @Valid SubmissionPayload submissionPayload) {
        Submission submission = getSubmissionOrElseThrow(id);
        if (!Status.DRAFT.equals(submission.getStatus())) {
            throw new LockedException("The submission is locked");
        }
        Submission newSubmission = submissionPayload.toSubmission();
        submission.setContent(newSubmission.getContent());
        submission.setStatus(newSubmission.getStatus());
        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.ok(new SubmissionResponse(saved));
    }

    @Operation(summary = "Assign submission", description = "Assign submission for specific specialist by email")
    @Validation400ApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful assign submission",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Submission.class))}),
            @ApiResponse(responseCode = "423", description = "The submission is assigned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Transactional
    @PutMapping("/submissions/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') and hasPermission(#id, @assignableSubmission)")
    public ResponseEntity<SubmissionResponse> assignSubmission(@PathVariable("id") Long id,
                                                       @RequestBody @Valid AssignPayload assignPayload) {
        Submission submission = getSubmissionOrElseThrow(id);
        SafeguardUser user = getUserOrElseThrow(assignPayload.getEmail());
        Predicate<Role> isReviewer = role -> (role == Role.ADMIN || role == Role.MANAGER || role == Role.SPECIALIST);
        if (!isReviewer.test(user.getRole())) {
            throw new IllegalArgumentException("The reviewer has no approval permission");
        }
        submission.setStatus(Status.ASSIGNED);
        submission.setAssignedUser(user);
        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.ok(new SubmissionResponse(saved));
    }

    @Operation(summary = "Update submission", description = "Update an existing submission with the provided details")
    @Validation400ApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of submission",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Submission.class))}),
            @ApiResponse(responseCode = "423", description = "The submission is assigned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Transactional
    @PutMapping("/submissions/{id}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST') and hasPermission(#id, @isAssignedUser)")
    public ResponseEntity<SubmissionResponse> approvalSubmission(@PathVariable("id") Long id,
                                                                 @RequestBody @Valid ApprovalPayload approvalPayload) {
        Submission submission = getSubmissionOrElseThrow(id);
        submission.setStatus(approvalPayload.getStatus());
        submission.setComment(approvalPayload.getComment());
        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.ok(new SubmissionResponse(saved));
    }

    @Operation(summary = "Delete submission", description = "Delete an existing submission")
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful deletion of submission")
    })
    @Transactional
    @DeleteMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER') and hasPermission(#id, @submissionOwner)")
    public ResponseEntity<Void> deleteSubmission(@PathVariable("id") Long id) {
        Submission submission = getSubmissionOrElseThrow(id);
        if (!Status.DRAFT.equals(submission.getStatus())) {
            throw new LockedException("The submission is locked");
        }
        submissionRepository.delete(submission);
        return ResponseEntity.noContent().build();
    }

    private Submission getSubmissionOrElseThrow(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission is not found with id: %s", id))
        );
    }

    private SafeguardUser getUserOrElseThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User is not found with email: %s", email))
        );
    }
}

