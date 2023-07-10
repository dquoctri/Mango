/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.security.refresh;

import com.dqtri.mango.core.security.TokenResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
@Qualifier("refreshTokenResolver")
public class RefreshTokenResolver implements TokenResolver {

    private final UserDetailsService userDetailsService;

    @Value("${core.auth.refresh.secretKey}")
    private String refreshSecretKey;

    @Override
    public Authentication verifyToken(String token) {
        Jws<Claims> claimsJws = validateRefreshToken(token);
        if (claimsJws == null) return null;
        Claims body = claimsJws.getBody();
        String subject = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        return new RefreshAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    public Jws<Claims> validateRefreshToken(String refreshToken) {
        try {
            byte[] decode = Base64.getDecoder().decode(getRefreshSecretKey());
            return Jwts.parser().setSigningKey(decode).parseClaimsJws(refreshToken);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.", ex);
        }
        return null;
    }

    private String getRefreshSecretKey() {
        return this.refreshSecretKey;
    }
}
