/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.audit;

public class AuditInfoHolder {
    private AuditInfoHolder(){}
    private static class SingletonHelper {
        private static final AuditInfoHolder INSTANCE = new AuditInfoHolder();
    }
    public static AuditInfoHolder getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final ThreadLocal<AuditInfo> delegate = new ThreadLocal<>();

    public AuditInfo getCurrent() {
        AuditInfo session = delegate.get();
        if (session != null) {
            return session;
        }
        throw new IllegalArgumentException("Audit Holder is not configured correctly");
    }

    public void setCurrentContext(AuditInfo auditInfo) {
        delegate.set(auditInfo);
    }

    public void incorrectCleanup() {
        delegate.remove(); // Compliant
    }
}
