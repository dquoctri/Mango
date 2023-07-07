/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.audit;

import com.dqtri.mango.submission.model.AuditRevisionEntity;
import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditInfo audit = AuditInfoHolder.getInstance().getCurrent();
        if (revisionEntity instanceof AuditRevisionEntity auditRevisionEntity){
            auditRevisionEntity.setAction(audit.getAction());
            auditRevisionEntity.setMethod(audit.getMethod());
            auditRevisionEntity.setUri(audit.getUri());
            auditRevisionEntity.setEmail(audit.getEmail());
            auditRevisionEntity.setDescription(audit.getDescription());
        }
    }
}
