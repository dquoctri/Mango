/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.security.access;

import com.dqtri.mango.submission.model.SafeguardUser;
import com.dqtri.mango.submission.repository.UserRepository;
import com.dqtri.mango.submission.security.AppUserDetails;
import com.dqtri.mango.submission.security.TokenResolver;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
@Qualifier("accessTokenResolver")
public class AccessTokenResolver implements TokenResolver {

    private final UserRepository userRepository;
    @Value("${safeguard.auth.access.publicKey}")
    private String accessPublicKey;

    @Override
    public Authentication verifyToken(String accessToken) {
        Jws<Claims> claimsJws = validateAccessToken(accessToken);
        if (claimsJws == null) return null;
        Claims body = claimsJws.getBody();
        String email = body.getSubject();
        SafeguardUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username not found: %s", email)));
        AppUserDetails userDetails = AppUserDetails.create(user);
        return new AccessAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private PublicKey getAccessPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(this.accessPublicKey));
        return keyFactory.generatePublic(keySpec);
    }

    public Jws<Claims> validateAccessToken(String accessToken) {
        try {
            PublicKey publicKey = getAccessPublicKey();
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(accessToken);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty", ex);
        } catch (NoSuchAlgorithmException ex) {
            log.error("The JWT does not reflect this algorithm", ex);
        } catch (InvalidKeySpecException ex) {
            log.error("Invalid public key spec", ex);
        }
        return null;
    }

}
