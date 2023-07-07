/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.permissions;

import org.springframework.security.core.Authentication;

import java.io.Serializable;

public abstract class Permission {
    public abstract boolean isAllowed(Authentication authentication, Object targetDomainObject);

    public abstract boolean isAllowed(Authentication authentication, Serializable targetId, String targetType);
}
