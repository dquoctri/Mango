package com.dqtri.mango.core.security.permissions;

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
        if (permission instanceof Permission) {
            return ((Permission) permission).isAllowed(authentication, targetDomainObject);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                 Object permission) {
        if (permission instanceof Permission) {
            return ((Permission) permission).isAllowed(authentication, targetId, targetType);
        }
        return false;
    }
}
