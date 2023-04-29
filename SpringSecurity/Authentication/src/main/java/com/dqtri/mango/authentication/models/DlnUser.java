package com.dqtri.mango.authentication.models;

import com.dqtri.mango.authentication.models.enums.Role;
import com.dqtri.mango.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dln_user")
@Setter
@Getter
public class DlnUser extends BaseEntity {

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Column(name = "role", length = 25, nullable = false)
    private Role role;

    @JsonIgnore
    @Column(name = "hash_password", length = 100, nullable = false)
    private String hashPassword;

    @JsonIgnore
    @Column(name = "salt", length = 15, nullable = false)
    private String salt;
}
