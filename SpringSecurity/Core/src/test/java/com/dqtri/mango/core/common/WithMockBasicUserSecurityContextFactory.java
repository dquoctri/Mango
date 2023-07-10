/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.common;

import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.security.BasicUserDetails;
import com.dqtri.mango.core.security.refresh.RefreshAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WithMockBasicUserSecurityContextFactory implements WithSecurityContextFactory<WithMockBasicUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockBasicUser customUser) {
        String email = StringUtils.hasLength(customUser.email()) ? customUser.email() : customUser.value();
        Assert.notNull(email, () -> customUser + " cannot have null username on both username and value properties");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : customUser.authorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        if (grantedAuthorities.isEmpty()) {
            for (String role : customUser.roles()) {
                Assert.isTrue(!role.startsWith("ROLE_"), () -> "roles cannot start with ROLE_ Got " + role);
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        else if (!(customUser.roles().length == 1 && "USER".equals(customUser.roles()[0]))) {
            throw new IllegalStateException("You cannot define roles attribute " + Arrays.asList(customUser.roles())
                    + " with authorities attribute " + Arrays.asList(customUser.authorities()));
        }
        SafeguardUser safeguardUser = new SafeguardUser();
        safeguardUser.setEmail(email);
        safeguardUser.setPassword(customUser.password());
        safeguardUser.setRole(customUser.role());

        BasicUserDetails basicUserDetails = new BasicUserDetails(safeguardUser);
        Authentication authentication = new RefreshAuthenticationToken(basicUserDetails, grantedAuthorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
