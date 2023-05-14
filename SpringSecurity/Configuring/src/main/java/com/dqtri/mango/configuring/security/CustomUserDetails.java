package com.dqtri.mango.configuring.security;

import com.dqtri.mango.configuring.model.ConfigUser;
import com.dqtri.mango.configuring.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
public class CustomUserDetails extends User implements UserDetails {

    @NotNull
    ConfigUser configUser;

    public CustomUserDetails(@NotNull ConfigUser user) {
        super(user.getEmail(), "", createAuthoritiesWithRole(user.getRole()));
        this.configUser = user;
    }

    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole(@NotNull Role role) {
        return List.of(
                new SimpleGrantedAuthority(role.name()),
                new SimpleGrantedAuthority("ROLE_" + role));
    }
}