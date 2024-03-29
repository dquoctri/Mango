/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.permissions;

import com.dqtri.mango.submission.exception.LockedException;
import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.model.Submission;
import com.dqtri.mango.submission.model.enums.Role;
import com.dqtri.mango.submission.model.enums.Status;
import com.dqtri.mango.submission.repository.SubmissionRepository;
import com.dqtri.mango.submission.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Component("assignableSubmission")
public class AssignablePermission extends Permission {

    private final SubmissionRepository submissionRepository;

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
        long submissionId = (long) targetDomainObject;
        Submission submission = getSubmissionOrElseThrow(submissionId);
        Predicate<Status> isLocked = state -> (state == Status.APPROVED || state == Status.REJECTED);
        if (isLocked.test(submission.getStatus())) {
            throw new LockedException("The submission is not assignable");
        }
        if (Status.SUBMITTED.equals(submission.getStatus())) return true;
        AppUserDetails currentUser = (AppUserDetails) authentication.getPrincipal();
        SafeguardUser safeguardUser = currentUser.getSafeguardUser();
        Predicate<Status> isUrgentModifyState  = state -> (state == Status.DRAFT || state == Status.ASSIGNED);
        return isUrgentModifyState.test(submission.getStatus()) && Role.ADMIN.equals(safeguardUser.getRole());
    }

    private Submission getSubmissionOrElseThrow(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission is not found with id: %s", id))
        );
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable targetId, String targetType) {
        return false;
    }
}
