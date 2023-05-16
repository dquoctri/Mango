package com.dqtri.mango.configuring.security;

import com.dqtri.mango.configuring.model.ConfigUser;
import com.dqtri.mango.configuring.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final String BEARER = "Bearer ";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        validate(authentication);
        if (authentication.getName().startsWith(BEARER)) {
            String token = authentication.getName().substring(BEARER.length());
            if ("header.payload.signature".equals(token)) {
                ConfigUser configUser = new ConfigUser();
                configUser.setEmail("submitter@mango.dqtri.com");
                configUser.setRole(Role.SUBMITTER);
                CustomUserDetails customUserDetails = new CustomUserDetails(configUser);
                return new CustomAuthenticationToken(customUserDetails, customUserDetails.getAuthorities());
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(CustomAuthenticationToken.class, authentication, "Only Accepts Custom Token");
    }
}
