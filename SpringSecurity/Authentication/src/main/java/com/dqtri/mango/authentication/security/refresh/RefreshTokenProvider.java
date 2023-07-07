/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.refresh;

import com.dqtri.mango.authentication.security.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Qualifier("refreshTokenProvider")
public class RefreshTokenProvider implements TokenProvider {

    @Value("${safeguard.auth.issuer}")
    private String issuer;

    @Value("${safeguard.auth.refresh.secretKey}")
    private String refreshSecretKey;

    @Value("${safeguard.auth.refresh.expirationInMs}")
    private long refreshExpirationInMs;

    @Override
    public String generateToken(Authentication authentication) {
        validate(authentication);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return generateRefreshToken(principal);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, "Only Accepts Core Token");
    }

    private String generateRefreshToken(UserDetails principal) {
        Date expiryDate = getRefreshExpiryDate();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, getRefreshSecretKey())
                .compact();
    }

    private Date getRefreshExpiryDate() {
        Date now = new Date();
        return new Date(now.getTime() + this.refreshExpirationInMs);
    }

    private byte[] getRefreshSecretKey() {
        return Base64.getDecoder().decode(this.refreshSecretKey);
    }
}
