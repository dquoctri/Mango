/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true, exclude = {"expirationDate"})
@ToString(callSuper = true)
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken extends BaseEntity implements Serializable {

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "is_dirty")
    private boolean isDirty;

    @ManyToOne(targetEntity = SafeguardUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_fk")
    private SafeguardUser user;
}
