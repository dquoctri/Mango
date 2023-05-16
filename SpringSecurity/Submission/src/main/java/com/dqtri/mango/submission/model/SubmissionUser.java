package com.dqtri.mango.submission.model;

import com.dqtri.mango.common.model.BaseEntity;
import com.dqtri.mango.submission.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "submission_user")
public class SubmissionUser extends BaseEntity {

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 25, nullable = false)
    private Role role;
}
