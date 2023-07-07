/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.audit;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AuditActionAspectExecutor {
    @Before(value = "execution(* *(..)) && @annotation(action)", argNames = "action")
    public void setAuditAction(AuditAction action) {
        AuditInfo audit = AuditInfoHolder.getInstance().getCurrent();
        if (audit.getAction() == null || "undefined".equalsIgnoreCase(audit.getAction())) {
            audit.setAction(action.value());
            audit.setDescription(action.value());
        }
    }
}
