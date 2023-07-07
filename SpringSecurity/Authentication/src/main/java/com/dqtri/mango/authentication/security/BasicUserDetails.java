/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.model.SafeguardUser;
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
public class BasicUserDetails extends User implements UserDetails {

    @NotNull
    private SafeguardUser safeguardUser;

    public BasicUserDetails(@NotNull SafeguardUser user) {
        super(user.getEmail(), user.getPassword(), createRefreshAuthorities());
        this.safeguardUser = user;
    }

    public static BasicUserDetails create(@NotNull SafeguardUser user) {
        return new BasicUserDetails(user);
    }

    private static List<SimpleGrantedAuthority> createRefreshAuthorities() {
        return List.of(new SimpleGrantedAuthority("REFRESH"), new SimpleGrantedAuthority("ROLE_REFRESH"));
    }
}