package com.dqtri.mango.authentication.model;

import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "mango_user")
public class MangoUser extends BaseEntity {

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Column(name = "role", length = 25, nullable = false)
    private Role role;

    @JsonIgnore
    @Column(name = "password", length = 60, nullable = false)
    private String password;
}
