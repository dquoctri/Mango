/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.permissions;

import com.dqtri.mango.authentication.model.SafeguardUser;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Component("updatableResource")
public class UpdatableResourcePermission extends Permission {

    private final UserRepository userRepository;

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
        Long userId = (Long) targetDomainObject;
        SafeguardUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));

        return !Role.ADMIN.equals(user.getRole());
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable targetId, String targetType) {
        return false;
    }
}
