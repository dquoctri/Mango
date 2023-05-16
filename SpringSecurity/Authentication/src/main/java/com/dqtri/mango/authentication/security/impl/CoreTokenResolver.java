package com.dqtri.mango.authentication.security.impl;

import com.dqtri.mango.authentication.security.CoreAuthenticationToken;
import com.dqtri.mango.authentication.security.CoreUserDetails;
import com.dqtri.mango.authentication.security.TokenResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoreTokenResolver implements TokenResolver {

    private final UserDetailsService userDetailsService;

    @Value("${authentication.auth.publicKey}")
    private String publicKey;

    @Override
    public Authentication verifyToken(String accessToken) {
        Jws<Claims> claimsJws = validateToken(accessToken);
        Claims body = claimsJws.getBody();
        Object authorities = body.get("authorities");
        String subject = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        if (userDetails instanceof CoreUserDetails coreUser) {
            return new CoreAuthenticationToken(coreUser, coreUser.getAuthorities());
        }
        return new CoreAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(this.publicKey));
        return keyFactory.generatePublic(KeySpec);
    }

    public Jws<Claims> validateToken(String accessToken) {
        try {
            PublicKey publicKey = getPublicKey();
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(accessToken);
        } catch (Exception ex) {
            log.error("Invalid JWTs", ex);
        }
        return null;
    }

}
