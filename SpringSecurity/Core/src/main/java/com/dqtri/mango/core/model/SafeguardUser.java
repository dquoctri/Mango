/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model;

import com.dqtri.mango.core.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.io.Serializable;


@Data
@Setter
@Getter
@ToString(callSuper = true, exclude = {"password"})
@EqualsAndHashCode(callSuper = true, exclude = {"password"})
@Audited
@Entity
@Table(name = "safeguard_user")
public class SafeguardUser extends BaseEntity implements Serializable {

    @Schema(example = "registered@dqtri.com")
    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Schema(example = "SUBMITTER")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 25, nullable = false)
    private Role role;

    @Schema(hidden = true)
    @JsonIgnore
    @Column(name = "password", length = 60, nullable = false)
    private String password;
}


