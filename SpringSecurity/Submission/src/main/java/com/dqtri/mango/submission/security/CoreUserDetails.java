/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security;

import com.dqtri.mango.submission.model.SubmissionUser;
import com.dqtri.mango.submission.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
public class CoreUserDetails extends User implements UserDetails {

    @NotNull
    private SubmissionUser submissionUser;

    public CoreUserDetails(@NotNull SubmissionUser user) {
        super(user.getEmail(), null, createAuthoritiesWithRole(user.getRole()));
        this.submissionUser = user;
    }

    public static CoreUserDetails create(@NotNull SubmissionUser user) {
        return new CoreUserDetails(user);
    }

    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole(@NotNull Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()), new SimpleGrantedAuthority("ROLE_" + role));
    }
}
