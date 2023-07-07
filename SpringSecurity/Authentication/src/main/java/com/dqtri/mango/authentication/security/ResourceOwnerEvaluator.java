/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.security.permissions.Permission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Component
public class ResourceOwnerEvaluator implements PermissionEvaluator {
    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (permission instanceof Permission instance) {
            return instance.isAllowed(authentication, targetDomainObject);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                 Object permission) {
        if (permission instanceof Permission instance) {
            return instance.isAllowed(authentication, targetId, targetType);
        }
        return false;
    }
}
