/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.models;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
public class MangoUserDetails extends User implements UserDetails {

    @NotNull
    private MangoUser user;

    public MangoUserDetails(@NotNull MangoUser user) {
        super(user.getEmail(), user.getPassword(), createAuthoritiesWithRole(user.getRole()));
        this.user = user;
    }

    public static MangoUserDetails create(MangoUser user) {
        return new MangoUserDetails(user);
    }

    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole(Role role) {
        return List.of(
                new SimpleGrantedAuthority(role.name()),
                new SimpleGrantedAuthority("ROLE_" + role));
    }
}
