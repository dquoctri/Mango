/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.permissions;

import com.dqtri.mango.authentication.model.SafeguardUser;
import com.dqtri.mango.authentication.model.Submission;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.model.enums.Status;
import com.dqtri.mango.authentication.repository.SubmissionRepository;
import com.dqtri.mango.authentication.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Component("isAssignedUser")
public class AssignedUserPermission extends Permission {

    private final SubmissionRepository submissionRepository;

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
        long submissionId = (long) targetDomainObject;
        Submission submission = getSubmissionOrElseThrow(submissionId);
        Predicate<Status> isWaitingForApproval = state -> (state == Status.ASSIGNED);
        if (!isWaitingForApproval.test(submission.getStatus())) {
            throw new IllegalArgumentException("The submission has invalid state");
        }
        AppUserDetails currentUser = (AppUserDetails) authentication.getPrincipal();
        SafeguardUser safeguardUser = currentUser.getSafeguardUser();
        if (Role.ADMIN.equals(safeguardUser.getRole())) return true;

        return safeguardUser.equals(submission.getAssignedUser());
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