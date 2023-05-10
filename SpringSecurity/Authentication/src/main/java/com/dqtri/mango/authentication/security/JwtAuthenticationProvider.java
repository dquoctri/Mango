package com.dqtri.mango.authentication.security.jwt;

import com.dqtri.mango.authentication.model.MangoUser;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.security.models.MangoUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Primary
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private static final String BEARER = "Bearer ";

    private final JwtTokenService jwtTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            validate(authentication);
            if (authentication.getName().startsWith(BEARER)) {
                String token = authentication.getName().substring(BEARER.length());
                MangoUser mangoUser = new MangoUser();
                mangoUser.setEmail("submitter");
                mangoUser.setPassword("abc");
                mangoUser.setRole(Role.SUBMITTER);
                MangoUserDetails mangoUserDetails = new MangoUserDetails(mangoUser);
                return new JwtAuthenticationToken(mangoUserDetails, mangoUserDetails.getAuthorities());
                //return jwtTokenService.verifyToken(token);
            }
        } catch (UsernameNotFoundException ex) {
            log.error(ex.getMessage());
        } catch (Exception e) {
            log.error("Authentication failed", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication, "Only Accepts JWT Token");
    }
}
