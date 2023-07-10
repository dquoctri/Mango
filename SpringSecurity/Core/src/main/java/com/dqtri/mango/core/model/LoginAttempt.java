/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "login_attempt")
public class LoginAttempt extends BaseEntity implements Serializable {

    public LoginAttempt(String email){
        this.email = email;
        this.failedAttempts = 0;
        this.lastFailedTimestamp = new Date().getTime();
        this.isLockout = false;
    }

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Min(0)
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "last_failed_timestamp", nullable = false)
    private long lastFailedTimestamp;

    @Column(name = "lockout_status", nullable = false)
    private boolean isLockout = false;
}

