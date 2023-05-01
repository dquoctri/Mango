/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.models;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.model.enums.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class DlnUserDetails extends User implements UserDetails {

    @NonNull
    private MangoUser user;

    public DlnUserDetails(@NonNull MangoUser user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.user = user;
    }

    public static DlnUserDetails create(MangoUser user) {
        List<SimpleGrantedAuthority> authorities = createAuthoritiesWithRole(user.getRole());
        return new DlnUserDetails(user, authorities);
    }
    
    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole(Role role){
        return List.of(
                new SimpleGrantedAuthority(role.name()),
                new SimpleGrantedAuthority("ROLE_" + role));
    }
}
