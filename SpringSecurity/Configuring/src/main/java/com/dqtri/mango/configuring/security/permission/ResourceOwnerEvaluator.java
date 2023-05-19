package com.dqtri.mango.configuring.security.permission;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ResourceOwnerEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (permission instanceof Permission instance) {
            return instance.isAllowed(authentication, targetDomainObject);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                 Object permission) {
        if (permission instanceof Permission instance) {
            return instance.isAllowed(authentication, targetId, targetType);
        }
        return false;
    }
}
