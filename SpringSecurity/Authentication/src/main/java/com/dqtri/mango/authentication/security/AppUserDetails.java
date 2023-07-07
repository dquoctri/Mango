/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.model.SafeguardUser;
import com.dqtri.mango.authentication.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class AppUserDetails extends User implements UserDetails {
    @NotNull
    private SafeguardUser safeguardUser;

    public AppUserDetails(@NotNull SafeguardUser user) {
        super(user.getEmail(), user.getPassword(), createAuthoritiesWithRole(user.getRole()));
        this.safeguardUser = user;
    }

    public static AppUserDetails create(@NotNull SafeguardUser user) {
        return new AppUserDetails(user);
    }

    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole(@NotNull Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()), new SimpleGrantedAuthority("ROLE_" + role));
    }
}
