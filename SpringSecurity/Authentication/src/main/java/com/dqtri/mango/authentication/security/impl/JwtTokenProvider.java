/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.model.dto.response.TokenResponse;
import com.dqtri.mango.authentication.security.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    @Value("${authentication.auth.issuer}")
    private String issuer;

    @Value("${authentication.auth.privateKey}")
    private String privateKey;

    @Value("${authentication.auth.expirationInMs}")
    private long expirationInMs;

    @Override
    public TokenResponse generateToken(Authentication authentication) throws Exception {
        validate(authentication);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Date expiryDate = getExpiryDate();
        String accessToken = Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("authorities", principal.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact();
        return new TokenResponse(accessToken);
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(this.privateKey));
        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    private Date getExpiryDate() {
        Date now = new Date();
        return new Date(now.getTime() + expirationInMs);
    }

    private void validate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is missing");
        Assert.notNull(authentication.getPrincipal(), "Authentication principal is missing");
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, "Only Accepts Core Token");
    }
}
