/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.refresh;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class RefreshAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    public RefreshAuthenticationToken(String token) {
        super(null);
        this.principal = token;
    }

    public RefreshAuthenticationToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RefreshAuthenticationToken that = (RefreshAuthenticationToken) o;

        return Objects.equals(principal, that.principal);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (principal != null ? principal.hashCode() : 0);
        return result;
    }
}
