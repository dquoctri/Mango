package com.dqtri.mango.configuring.secirity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails() {
        super("submitter@mango.dqtri.com", "$2a$10$BEviOVmsh3O8vJ8/kSxvyObKTk.PrqAS/RfL3ZtNXpjpimXun4f0S", createAuthoritiesWithRole());
    }

    private static List<SimpleGrantedAuthority> createAuthoritiesWithRole() {
        return List.of(
                new SimpleGrantedAuthority("SUBMITTER"),
                new SimpleGrantedAuthority("ROLE_SUBMITTER"));
    }
}