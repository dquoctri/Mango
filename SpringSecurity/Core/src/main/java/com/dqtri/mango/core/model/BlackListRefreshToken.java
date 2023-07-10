/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@ToString(callSuper = true)
@Entity
@EqualsAndHashCode(callSuper = false, exclude = "expirationDate")
@Table(name = "black_list_refresh_token")
public class BlackListRefreshToken extends BaseEntity implements Serializable {

    @Column(name = "email", length = 320, nullable = false)
    private String email;

    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;
}

