/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.model;

import com.dqtri.mango.submission.audit.AuditRevisionListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.io.Serializable;

@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@RevisionEntity(AuditRevisionListener.class)
@Table(name = "revinfo")
public class AuditRevisionEntity extends DefaultRevisionEntity implements Serializable {

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "method", length = 10)
    private String method;

    @Column(name = "uri", length = 510)
    private String uri;

    @Column(name = "email", length = 152)
    private String email;

    @Column(name = "description", length = 300)
    private String description;
}