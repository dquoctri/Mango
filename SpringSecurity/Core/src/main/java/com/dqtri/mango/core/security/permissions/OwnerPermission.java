/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.permissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Component("ownerPermission")
public class OwnerPermission extends Permission {

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
        return true;
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable targetId, String targetType) {
        return false;
    }
}
